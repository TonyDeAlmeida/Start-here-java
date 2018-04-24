package com.orange.eventProcessingSample.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.orange.eventProcessingSample.util.json.devData.LoFiringRule;
import com.orange.eventProcessingSample.util.json.devData.LoMatchingReturn;

/**
 * This utility will create event processing rule
 **/
public class CreateRule {

	// Connection parameters
	static private String SERVER = "https://liveobjects.orange-business.com/api/v0/"; // declare endpoints
	public static String API_KEY = "enter your key here";                             // <-- REPLACE by YOUR API_KEY!

	// Publication parameters

	public static void main(String[] args) {
		String matchingRuleId = createMatchingRule();
		createFiringRule(matchingRuleId);

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

	private static void createFiringRule(String matchingRuleId) {
		
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
			

			System.out.println(response.readEntity(String.class));
			

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
