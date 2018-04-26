package com.orange.stateChangeSample;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.orange.stateChangeSample.json.ActionPolicy;
import com.orange.stateChangeSample.json.Actions;
import com.orange.stateChangeSample.json.EmailAction;
import com.orange.stateChangeSample.json.LoStateRule;
import com.orange.stateChangeSample.json.Trigger;

/**
 * This utility will create event processing rule
 **/
public class CreateRule {

	// Connection parameters
	static private String SERVER = "https://liveobjects.orange-business.com/api/v0/"; // declare endpoints
	public static String API_KEY = "enter your key here"; 							  // <-- REPLACE by YOUR API_KEY!
	public static String EMAIL   = "enter the email here";							  // <-- REPLACE by YOUR email !

	
	public static void main(String[] args) {
		String stateRuleId = createStateRule();
		createActionPolicy(stateRuleId);           
	}
	
	private static String createStateRule() {
		String stateRuleId = null;
		
		String serial="{\"name\": \"stateRule sample\",\"enabled\":true, \"stateFunction\":{\"if\": [ {\"<\":[{\"var\":\"value.temperature\"},20]},\"cold\",{\">\":[{\"var\": \"value.temperature\"},20]},\"hot\"]}}]";
		
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("eventprocessing/stateprocessing-rule");

			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE).header("X-API-Key",API_KEY);

			Invocation invocation =invocationBuilder.buildPost(Entity.entity(serial, MediaType.APPLICATION_JSON_TYPE));
			
			Response response=invocation.invoke();
			
			LoStateRule resp = response.readEntity(LoStateRule.class);
			stateRuleId = resp.id;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stateRuleId;

	}

	
	private static void createActionPolicy(String stateRuleId) {
		
		ActionPolicy actionPolicy = new ActionPolicy();
		
		actionPolicy.enabled=true;
		actionPolicy.name="sample action";
		Trigger triggers = new Trigger();
		triggers.eventRuleIds = new String[1];
		triggers.eventRuleIds[0] = stateRuleId;
		actionPolicy.triggers =triggers;
		
		EmailAction[] emailAction = new EmailAction[1];
		emailAction[0] = new EmailAction();
		// to cc and cci
		// here only one "to" email set, but feel free and adapt your test to multiple to, cc or cci
		emailAction[0].to= new String[1];
		emailAction[0].to[0]= EMAIL;

		emailAction[0].contentTemplate ="{{stateKey}} change from state {{previousState}} to state {{newState}} at {{timestamp}}";
		emailAction[0].subjectTemplate="State change for {{data.metadata.source}}";
		
		Actions actions = new Actions();
		actions.emails = emailAction;
		
		actionPolicy.actions = actions;
		Gson gson = new Gson();
		String serial = gson.toJson(actionPolicy, ActionPolicy.class);
		
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("event2action/actionPolicies");

			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE).header("X-API-Key",API_KEY);

			Invocation invocation =invocationBuilder.buildPost(Entity.entity(serial, MediaType.APPLICATION_JSON_TYPE));
			
			Response response=invocation.invoke();
			String resp = response.readEntity(String.class);
			System.out.println(resp);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
