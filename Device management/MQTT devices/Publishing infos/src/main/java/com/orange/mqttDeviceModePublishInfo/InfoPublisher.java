package com.orange.mqttDeviceModePublishInfo;

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
import com.orange.mqttDeviceModePublishInfo.json.devInfo.LoDeviceStatus;
import com.orange.mqttDeviceModePublishInfo.json.devInfo.SampleStatus;

/**
 * This sample will publish data for a device : device1
 * At this end you can take a look in the data zone of live objects to see the data sent.
 * You may also, when familiar with the concepts create a fifo (BEFORE SENDING DATA !!)
 * and consume the data sent form this fifo using the MqttFifoSample (FifoConsumer.java)
 **/
public class InfoPublisher {
	// Connection parameters
	public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY   = "enter your api key here";           // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME  = "json+device";								   // The option to publish in device mode
	public static String CLIENT_ID = "urn:lo:nsid:samples:device1";				   // in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}
	
	
	//Publication parameters
	public static String TOPIC="dev/info";	// topic to publish to
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
			
			
			SampleStatus myStatus = new SampleStatus();
			LoDeviceStatus loStatus = new LoDeviceStatus();
			
			loStatus.info = myStatus;                             				 
			myStatus.ip = "4.4.4.7";
			myStatus.uptime=12458;
			
			Gson gson = new Gson();
			String msg = gson.toJson(loStatus);

			MqttMessage message = new MqttMessage(msg.getBytes());
			message.setQos(qos);
			sampleClient.publish(TOPIC, message);
			System.out.println("Message published on dev/info");

			
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
