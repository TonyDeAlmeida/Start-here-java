package com.orange.eventProcessingSample;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.orange.eventProcessingSample.util.json.devData.ActionPolicy;
import com.orange.eventProcessingSample.util.json.devData.Actions;
import com.orange.eventProcessingSample.util.json.devData.EmailAction;
import com.orange.eventProcessingSample.util.json.devData.LoFiringReturn;
import com.orange.eventProcessingSample.util.json.devData.LoFiringRule;
import com.orange.eventProcessingSample.util.json.devData.LoMatchingReturn;
import com.orange.eventProcessingSample.util.json.devData.Trigger;

/**
 * This utility will create event processing rule
 **/
public class CreateRule {

	// Connection parameters
	static private String SERVER = "https://liveobjects.orange-business.com/api/v0/"; // declare endpoints
	public static String API_KEY = "f6422939005946619dd8a2575db3cb76"; 							  // <-- REPLACE by YOUR API_KEY!
	public static String EMAIL   ="pserenne@e-novact.com";							  // <-- REPLACE by YOUR email !

	
	public static void main(String[] args) {
		String matchingRuleId = createMatchingRule();
		String firingRuleId = createFiringRule(matchingRuleId);
		createActionPolicy(firingRuleId);            // comment out this line if you don't need to send email when the rule fired
	}
	
	private static String createMatchingRule() {
		String matchingRuleId = null;
		
		String serial="{\"dataPredicate\":{\"<\": [ {\"var\":\"value.hygrometry\"},20]},\"enabled\":true,\"name\": \"Test1211\"}";
		
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("eventprocessing/matching-rule");

			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE).header("X-API-Key",API_KEY);

			Invocation invocation =invocationBuilder.buildPost(Entity.entity(serial, MediaType.APPLICATION_JSON_TYPE));
			
			Response response=invocation.invoke();
			

			LoMatchingReturn resp = response.readEntity(LoMatchingReturn.class);
			
			matchingRuleId = resp.id;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return matchingRuleId;

	}

	private static String createFiringRule(String matchingRuleId) {
		String firingRuleId = null;
		LoFiringRule firingRule = new LoFiringRule();
		
		firingRule.name="evtProcessingSampleFiringRule";
		firingRule.enabled = true;
		
		firingRule.matchingRuleIds = new String[1];
		firingRule.matchingRuleIds[0] = matchingRuleId;
		
		firingRule.aggregationKeys = new String[1];
		firingRule.aggregationKeys[0] = "metadata.source";
		
		firingRule.firingType = "ALWAYS";
		
		Gson gson = new Gson();
		String serial = gson.toJson(firingRule, LoFiringRule.class);
		
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("eventprocessing/firing-rule");

			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE).header("X-API-Key",API_KEY);

			Invocation invocation =invocationBuilder.buildPost(Entity.entity(serial, MediaType.APPLICATION_JSON_TYPE));
			
			Response response=invocation.invoke();
			LoFiringReturn resp = response.readEntity(LoFiringReturn.class);

			firingRuleId = resp.id;
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return firingRuleId;
	}
	
	private static void createActionPolicy(String firingRuleId) {
		System.err.println("Firing Rule = "+firingRuleId);
		
		ActionPolicy actionPolicy = new ActionPolicy();
		
		actionPolicy.enabled=true;
		actionPolicy.name="sample action";
		Trigger triggers = new Trigger();
		triggers.eventRuleIds = new String[1];
		triggers.eventRuleIds[0] = firingRuleId;
		actionPolicy.triggers =triggers;
		
		EmailAction[] emailAction = new EmailAction[1];
		emailAction[0] = new EmailAction();
		// to cc and cci
		// here only one "to" email set, but feel free and adapt your test to multiple to, cc or cci
		emailAction[0].to= new String[1];
		emailAction[0].to[0]= EMAIL;

		emailAction[0].contentTemplate ="Value raising the alarm {{matchingContext.data.value.hygrometry}}";
		emailAction[0].subjectTemplate="Hygrometry alarm from {{matchingContext.data.metadata.source}}";
		
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
