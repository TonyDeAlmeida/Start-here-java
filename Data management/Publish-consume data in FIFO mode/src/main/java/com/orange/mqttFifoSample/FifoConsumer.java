package com.orange.mqttFifoSample;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orange.mqttFifoSample.json.LoFifoData;


/**
 * Application connects to LO and consumes messages from a FIFO queue.
 *
 * You MUST first create a FIFO called "sampleFifo" in your LO account, before trying to consume from !
 *
 * if you launch multiple instances you will notice the load balancing of the messages sent)
 * 
 * if you launch the consumer after messages been published, these old messages will be delivered in the right order
 */

public class FifoConsumer {

	// Connection parameters
		public static String SERVER    = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
		public static String API_KEY   = "enter your api key here";           // <-- REPLACE by YOUR API_KEY!	
		public static String USERNAME  ="payload+bridge";							   // The option to publish in device mode
		public static String CLIENT_ID ="app:" + UUID.randomUUID().toString();	       // in bridge mode : should be of your choice
		
		//Publication parameters
		public static String TOPIC ="fifo/sampleFifo";	// topic to publish to
		public static int qos      = 1;              	// set the qos
		


	public static void main(String[] args) throws InterruptedException {

		int KEEP_ALIVE_INTERVAL = 30;// Must be <= 50

		MqttClient sampleClient = null;

		System.out.println("Runinng consumer appId=" + CLIENT_ID);
		try {
			sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());

			// register callback (to handle received commands
			sampleClient.setCallback(new SimpleMqttCallback(sampleClient));

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(USERNAME); // selecting mode "Bridge"
			connOpts.setPassword(API_KEY.toCharArray()); // passing API key value as password
			connOpts.setCleanSession(true);
			connOpts.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
			connOpts.setAutomaticReconnect(true);

			// Connection
			System.out.printf("Connecting to broker: %s ...%n", SERVER);
			sampleClient.connect(connOpts);
			System.out.println("... connected.");

			synchronized (sampleClient) {
				sampleClient.wait();
			}

		} catch (MqttException me) {
			me.printStackTrace();

		} finally {
			// close client
			if (sampleClient != null && sampleClient.isConnected()) {
				try {
					sampleClient.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Basic "MqttCallback" 
	 */
	public static class SimpleMqttCallback implements MqttCallbackExtended {
		private MqttClient mqttClient;

		public SimpleMqttCallback(MqttClient mqttClient) {
			this.mqttClient = mqttClient;
		}
		
		/**
		 * When connection is OK subscribe to the desired topic : here fifo/fifoSample
		 */
		public void connectComplete(boolean b, String s) {
			System.out.println("Connection is established");
			try {
				System.out.printf("Consuming from fifo : ", TOPIC);
				mqttClient.subscribe(TOPIC);
				System.out.println("... subscribed.");
			} catch (MqttException e) {
				System.out.println("Error during subscription");
			}
		}

		/**
		 * called when a new message is available
		 */
		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			System.out.println( "Received message from FIFO queue - " + mqttMessage);
			Gson gson = new Gson();
			JsonObject mqttPayload = gson.fromJson(new String(mqttMessage.getPayload()), JsonObject.class);
			LoFifoData loFifoData = gson.fromJson(mqttPayload, LoFifoData.class);
			System.out.println(loFifoData.value.payload);
		}

		public void connectionLost(Throwable throwable) {
			System.out.println("Connection lost");
		}
		
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// nothing
		}

		
	}
}
