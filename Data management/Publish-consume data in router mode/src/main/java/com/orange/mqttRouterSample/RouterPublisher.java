package com.orange.mqttRouterSample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;
import com.orange.mqttRouterSample.json.LoData;
import com.orange.mqttRouterSample.json.SampleData;

/**
 * Application connects to LO and publish messages to the router.
 *
 * Messages can be consume from a "routerConsumer", but your consumer application has to be running before you send messages from the publisher
 * You can also consume message in fifo mode (use the MqttFifoSample (FifoConsumer.java) after having declared a binding in Live Objects.
 * In that case your message can be consumed 
 * 
 */
public class RouterPublisher {
	
	// Connection parameters
	public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY   = "f6422939005946619dd8a2575db3cb76";           // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME  = "payload+bridge";								   // The option to publish in device mode
	public static String CLIENT_ID = "myClientId";				   // in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}
	
	//Publication parameters
	public static String TOPIC="router/~event/v1/data/new";	// topic to publish to
	public static int qos = 1;                              // set the qos 
	
	public static void main(String[] args) {

		try {
			// set client id to any value

			MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
			
			// create and fill you connections options
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			
			connOpts.setUserName(USERNAME);
			connOpts.setPassword(API_KEY.toCharArray());
			
			// now connect to LO
			sampleClient.connect(connOpts);
			System.out.println("Connected  in payload+bridge Mode");
			
			// create the message
			SampleData myData = new SampleData();
			LoData loData = new LoData();
			
			Date msgDt = new Date();
			loData.streamId="sampleStream2";
			loData.model="sampleModel2";
			loData.timestamp=toISO8601UTC(msgDt);
			loData.tags= new String[1];
			loData.tags[0]="myTag1";
			loData.value = myData;  
			
			for(int i=0;i < 10 ;i++) {
				myData.payload = "Message " + i +" from bridgeModeSample (payload+bridge) to routeur data on " + msgDt;
				
				Gson gson = new Gson();
				String msg = gson.toJson(loData);
				
				// send your messages
		
				MqttMessage message = new MqttMessage(msg.getBytes());
				message.setQos(qos);
				sampleClient.publish(TOPIC, message);
				System.out.println("Message published");
			}
			
			// diconnect
			sampleClient.disconnect();
			System.out.println("Disconnected");

		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}
	
	public static String toISO8601UTC(Date date) {
		  TimeZone tz = TimeZone.getTimeZone("UTC");
		  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		  df.setTimeZone(tz);
		  return df.format(date);
	}
}
