package com.orange.mqttDeviceModeUpdateConfig;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;
import com.orange.mqttDeviceModeSample.json.devCfg.LoCfgUpd;

/**
 * This sample will demonstrate how to update configuration information for a device : device1
 * Just update in Live Objects UI the values you have published with the ConfigPublisher and run the sample
 **/
public class ConfigUpdater {

	public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY = "f6422939005946619dd8a2575db3cb76";          // <-- REPLACE by YOUR API_KEY!
	public static String USERNAME = "json+device";                              // The option to publish in device mode
	public static String CLIENT_ID = "urn:lo:nsid:samples:device1";             // in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}

	// Publication parameters
	public static String TOPIC = "dev/cfg/upd"; // topic to subsribe from
	public static int qos = 1;
	public static int KEEP_ALIVE_INTERVAL = 30;// Must be <= 50

	public static void main(String[] args) throws InterruptedException {

		MqttClient mqttClient = null;

		try {
			mqttClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());

			// register callback (to handle received commands
			mqttClient.setCallback(new SimpleMqttCallback(mqttClient));

			MqttConnectOptions connOpts = new MqttConnectOptions();

			connOpts.setUserName(USERNAME); 

			connOpts.setPassword(API_KEY.toCharArray());
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

	/**
	 * Callback management for reading data
	 * @author fil
	 *
	 */
	public static class SimpleMqttCallback implements MqttCallbackExtended {
		private MqttClient mqttClient;

		public SimpleMqttCallback(MqttClient mqttClient) {
			this.mqttClient = mqttClient;
		}
		/**
		 * When connection is OK subscribe to the desired topic : here dev/cfg/upd
		 */
		public void connectComplete(boolean b, String s) {
			System.out.println("Connection is established");
			try {
				mqttClient.subscribe(TOPIC);
			} catch (MqttException e) {
				System.out.println("Error during subscription");
			}
		}
		/**
		 * called when a new message is available
		 */
		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			System.out.println("Received message from dev/cfg/upd - " + mqttMessage);
			Gson gson = new Gson();

			LoCfgUpd response = gson.fromJson(new String(mqttMessage.getPayload()), LoCfgUpd.class);
			publishResponse(response);
		}
		
		/**
		 * Publish the acknoledge to LO based on the cid of the update request received
		 * @param response
		 */
		public void publishResponse(LoCfgUpd response) {
			try {

				// set client id to device unique identifier (urn:lo:nsid:{namespace}:{id})
				MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());

				// create and fill you connections options
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				connOpts.setPassword(API_KEY.toCharArray());

				connOpts.setUserName(USERNAME); // use Device Mode

				// now connect to LO
				sampleClient.connect(connOpts);
				System.out.println("Connected in device Mode");

				// send your message to the right topic : dev/cfg to update config in Live Objects
				String topic = "dev/cfg";

				Gson gson = new Gson();
				String msg = gson.toJson(response); // we have updated all parameters so send all back

				MqttMessage message = new MqttMessage(msg.getBytes());
				message.setQos(1);
				sampleClient.publish(topic, message);
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
			
		public void connectionLost(Throwable throwable) {
			System.out.println("Connection lost");
		}

		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// nothing
		}
	}
}
