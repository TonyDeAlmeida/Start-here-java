package com.orange.mqttPubSubSample;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 
 * @author filou
 *
 */
public class PubSubPublisher {
	// Connection parameters
	public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY   = "f6422939005946619dd8a2575db3cb76";           // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME  = "payload+bridge";						   // The option to publish in device mode
	public static String CLIENT_ID = "myFifoClientId";				               // set client id to any value
	
	//Publication parameters
	public static String TOPIC="pubsub/pubSubSampleTopic";	// topic to publish to
	public static int qos = 1;           

	public static void main(String[] args) {
		
		try {
			MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());

			// create and fill you connections options
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName("payload+bridge");
			connOpts.setPassword(API_KEY.toCharArray());

			// now connect to LO
			sampleClient.connect(connOpts);
			System.out.println("Connected in bridgeMode");

			// send your message to topic
			String msg = "Message published on pubsub topic on " + new Date();

			MqttMessage message = new MqttMessage(msg.getBytes());
			message.setQos(qos);
			sampleClient.publish(TOPIC, message);
			System.out.println("Message published");


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
}
