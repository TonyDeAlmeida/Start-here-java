package com.orange.mqttPubSubSample;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Application connects to LO and consumes messages from a pubsub publication.
 *
 * You MUST first starts this consumer before publishing, else data will be lost !
 *
 */

public class PubSubConsumer {
	// Connection parameters
	public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY = "enter your api key here";   		// <-- REPLACE by YOUR API_KEY!
	public static String USERNAME = "payload+bridge"; 							// The option to publish in device mode
	public static String CLIENT_ID = "myFifoClientId"; 							// set client id to any value

	// Publication parameters
	public static String TOPIC = "pubsub/pubSubSampleTopic"; // topic to publish to
	public static int qos = 1;

	public static void main(String[] args) throws InterruptedException {

		int KEEP_ALIVE_INTERVAL = 30;// Must be <= 50

		MqttClient mqttClient = null;

		System.out.println("Runinng consumer appId=" + CLIENT_ID);
		try {
			mqttClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());

			// register callback (to handle received commands
			mqttClient.setCallback(new SimpleMqttCallback(mqttClient, CLIENT_ID));

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName("payload+bridge"); // selecting mode "Bridge"
			connOpts.setPassword(API_KEY.toCharArray()); // passing API key value as password
			connOpts.setCleanSession(true);
			connOpts.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
			connOpts.setAutomaticReconnect(true);

			// Connection
			System.out.printf("Connecting to broker: %s ...%n", SERVER);
			mqttClient.connect(connOpts);
			System.out.println("... connected.");

			synchronized (mqttClient) {
				mqttClient.wait();
			}

		} catch (MqttException me) {
			me.printStackTrace();

		} finally {
			// close client
			if (mqttClient != null && mqttClient.isConnected()) {
				try {
					mqttClient.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class SimpleMqttCallback implements MqttCallbackExtended {
		private MqttClient mqttClient;
		private String appId;

		public SimpleMqttCallback(MqttClient mqttClient) {
			this.mqttClient = mqttClient;
		}

		public SimpleMqttCallback(MqttClient mqttClient, String appId) {
			this(mqttClient);
			this.appId = appId;
		}

		public void connectionLost(Throwable throwable) {
			System.out.println("Connection lost");
		}

		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			System.out.println("Consumer appId=" + this.appId + " - Received message from pubsubTopic - " + mqttMessage);
		}

		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// nothing
		}

		public void connectComplete(boolean b, String s) {
			System.out.println("Connection is established");
			try {
				mqttClient.subscribe(TOPIC);
			} catch (MqttException e) {
				System.out.println("Error during subscription");
			}
		}
	}
}
