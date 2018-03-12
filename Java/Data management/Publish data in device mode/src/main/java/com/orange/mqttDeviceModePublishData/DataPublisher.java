package com.orange.mqttDeviceModePublishData;

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
import com.orange.mqttDeviceModePublishData.json.devData.LoData;
import com.orange.mqttDeviceModePublishData.json.devData.SampleData;


/**
 * This sample will publish data for a device : device1
 * At this end you can take a look in the data zone of live objects to see the data sent.
 * You may also, when familiar with the concepts consume from the data zone using one of the consumer samples
 **/
public class DataPublisher {
	
	// Connection parameters
	public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY   = "f6422939005946619dd8a2575db3cb76";           // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME  = "json+device";								   // The option to publish in device mode
	public static String CLIENT_ID = "urn:lo:nsid:samples:device1";				   // in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}
	
	//Publication parameters
	public static String TOPIC="dev/data";	// topic to publish to
	public static int qos = 1;              // set the qos
	
	public static void main(String[] args) {
		try {
			
			// set client id to device unique identifier (urn:lo:nsid:{namespace}:{id})
			MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
			
			// create and fill you connections options
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setPassword(API_KEY.toCharArray());
			
			connOpts.setUserName(USERNAME);  // use Device Mode
			
			// now connect to LO
			sampleClient.connect(connOpts);
			System.out.println("Connected in device Mode");
			
			// create message
			LoData loData = new LoData();
			SampleData myData = new SampleData();
			
			Date msgDt = new Date();
			loData.s="Stream1";
			loData.m="samplesModel";
			loData.ts=toISO8601UTC(msgDt);
			loData.loc = new double[2];
			loData.loc[0]=48.125;
			loData.loc[1]=2.185;
			loData.v = myData;   
			myData.payload = "Message from deviceMode on dev/data on " + msgDt;
			
			Gson gson = new Gson();
			String msg = gson.toJson(loData);

			// send your message 
			MqttMessage message = new MqttMessage(msg.getBytes());
			message.setQos(qos);
			sampleClient.publish(TOPIC, message);
			System.out.println("Message published");

			
			// disconnect
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
