package com.orange.mqttDeviceModeUpdateFirmware;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.Gson;
import com.orange.mqttDeviceModeSample.json.devResource.LoResourceUpd;
import com.orange.mqttDeviceModeSample.json.devResource.LoResourceUpdate;

public class ResourceUpdater {

	public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY = "f6422939005946619dd8a2575db3cb76"; // <-- REPLACE by YOUR API_KEY!
	public static String USERNAME = "json+device"; // The option to publish in device mode
	public static String CLIENT_ID = "urn:lo:nsid:samples:device1"; // in device mode : should be the syntax
																	// urn:lo:nsid:{namespace}:{id}

	// Publication parameters
	public static String TOPIC = "dev/rsc/upd"; // topic to subscribe from
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
				System.out.println("Error during subscription : " + e.getReasonCode());
			}
		}
		/**
		 * called when a new message is available
		 */
		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			System.out.println("Received message from dev/rsc/upd - " + mqttMessage);
			LoResourceUpd response = null;

			Gson gson = new Gson();
			response = gson.fromJson(new String(mqttMessage.getPayload()), LoResourceUpd.class);

			// do here what you need with the response
			// ...
			
			// prepare acknowldgment to Live Objects
			LoResourceUpdate updateResponse = new LoResourceUpdate();
			
			updateResponse.cid = response.cid;
//			The response you send (res member of the updateResponse) may be	:	
//		    "OK" : the update is accepted and will start,
//		    "UNKNOWN_RESOURCE" : the update is refused, because the resource (identifier) is unsupported by the device,
//		    "WRONG_SOURCE_VERSION" : the device is no longer in the "current" resource version specified in the resource update request,
//		    "INVALID_RESOURCE" : the requested new resource version has incorrect version format or metadata,
//		    "NOT_AUTHORIZED" : the device refuses to update the targeted resource (ex: bad timing, "read-only" resource, etc.)
//		    "INTERNAL_ERROR" : an error occured on the device, preventing for the requested resource update,

			updateResponse.res = "OK"; //

			
			publishResponse(updateResponse,mqttClient);
			
			// once the response is published, you can download the new firmware (here using commons-io library)
			FileUtils.copyURLToFile(new URL(response.m.uri), new File("NewFirmware"));
		}
		
		/**
		 * Publish the acknowledge to LO based on the cid of the update request received
		 * publish to the topic : dev/rsc/upd/res
		 * @param response
		 */
		public void publishResponse(LoResourceUpdate updateResponse,MqttClient mqqtClient) {
			try {

				// send your message to the right topic : dev/rsc/upd/res to update resource from Live Objects
				String topic = "dev/rsc/upd/res";

				Gson gson = new Gson();
				String msg = gson.toJson(updateResponse); // we have updated all parameters so send all back

				MqttMessage message = new MqttMessage(msg.getBytes());
				message.setQos(1);
				mqttClient.publish(topic, message);
				System.out.println("Message published");
				
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
