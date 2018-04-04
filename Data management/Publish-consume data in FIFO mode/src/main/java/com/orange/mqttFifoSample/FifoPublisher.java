package com.orange.mqttFifoSample;

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
import com.orange.mqttFifoSample.json.LoFifoData;
import com.orange.mqttFifoSample.json.SampleData;

/**
 * Application connects to LO and publish messages.
 *
 * You MUST first starts the consumer before publishing, else data will be lost !
 *
 */

public class FifoPublisher {
	// Connection parameters
	public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY   = "enter your api key here";           // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME  = "payload+bridge";						   // The option to publish in device mode
	public static String CLIENT_ID = "myFifoClientId";				               // set client id to any value
	
	//Publication parameters
	public static String TOPIC="fifo/sampleFifo";	// topic to publish to
	public static int qos = 1;              // set the qos
		
	public static void main(String[] args) {
		
		try {
			MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
			
			// create and fill you connections options
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			
			connOpts.setUserName(USERNAME);
			connOpts.setPassword(API_KEY.toCharArray());
			
			// now connect to LO
			sampleClient.connect(connOpts);
			System.out.println("Connected  in bridge Mode");
			
			// create message
			SampleData myData = new SampleData();
			LoFifoData loData = new LoFifoData();
			
			Date msgDt = new Date();
			loData.streamId="sampleStream";
			loData.model="sampleModel";
			loData.timestamp=toISO8601UTC(msgDt);
			loData.value = myData;                             				 
			myData.payload = "Message from bridgeModeSample (payload+bridge) to fifo/sampleFifo on " + msgDt;
			
			Gson gson = new Gson();
			String msg = gson.toJson(loData);

			// send your message 
			for (int i=0;i<3;i++) {		
				MqttMessage message = new MqttMessage(msg.getBytes());
				message.setQos(qos);
				sampleClient.publish(TOPIC, message);
				System.out.println("Message published");
			}
			
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
