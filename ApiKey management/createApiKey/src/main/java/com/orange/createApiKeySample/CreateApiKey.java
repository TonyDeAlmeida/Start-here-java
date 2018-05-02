package com.orange.createApiKeySample;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.orange.createApiKeySample.json.ApiKey;

/**
 * This utility will create a new API_KEY to use it from your device
 * To get your tenantId and parentId, you can use the swagger tool
 * "Listing all the API keys of the connected Tenant" 
 * from https://liveobjects.orange-business.com/#/faq -> swagger/API keys
 **/
public class CreateApiKey {

	// Connection parameters
	static private String SERVER = "https://liveobjects.orange-business.com/api/v0/"; // declare endpoints
	public static String API_KEY = "enter your api key here"; 				                // <-- REPLACE by YOUR API_KEY!
	public static String TENANT_ID="enter your tenant id here";						            // <-- REPLACE by your Tenant ID
	public static String PARENT_ID="enter your parent key id here";						        // <-- REPLACE by the API key of your master key
	
	public static void main(String[] args) {
		String newApiKey = createApiKey();
		System.out.println(" created api key : " + newApiKey);
	}
	
	private static String createApiKey() {
		ApiKey resp = null;
		
		ApiKey apiKey = new ApiKey();
		apiKey.tenantId = TENANT_ID;
		apiKey.parentId = PARENT_ID;
		apiKey.active   = true;
		apiKey.label    = "myKey";
		apiKey.description = "rest created key";
		apiKey.roles = new String[1];
		apiKey.roles[0] ="DEVICE_ACCESS";

		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("apiKeys");

			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE).header("X-API-Key",API_KEY);

			Invocation invocation =invocationBuilder.buildPost(Entity.entity(apiKey, MediaType.APPLICATION_JSON_TYPE));
			
			Response response=invocation.invoke();
			
			resp = response.readEntity(ApiKey.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp.value;
	}	
}
