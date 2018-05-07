package com.orange.multiCtiteriaSearch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This class will perform a search :
 * 
 * max result : 10
 * temperature betwwen 20 and 28
 * hygrometry >= 10
 * 
 * To test it you can use the DataPublisher sample, run it many time, changing temperature and hygrometry
 * 
 **/
public class Search {

	// Connection parameters
	static private String SERVER = "https://liveobjects.orange-business.com/api/v0/"; // declare endpoints
	public static String API_KEY = "enter your api key here"; 					        		  // <-- REPLACE by YOUR API_KEY!

	
	public static void main(String[] args) {
		
		
		String serial="{\"size\" : 100,\"query\" : {\"and\": [{\"range\" : {\"@samplesModel.value.temperature\" : { \"gte\": 20, \"lte\" :28  }}},{\"range\" : {\"@samplesModel.value.hygrometry\" : { \"gte\": 10 }}}]}}";
		
		
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER).path("data/search");

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
