package com.orange.mqttDeviceModePublishFirmware;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.Gson;
import com.orange.mqttDeviceModePublishFirmware.json.devResource.LoResource;
import com.orange.mqttDeviceModePublishFirmware.json.devResource.SampleFirmware;
import com.orange.mqttDeviceModePublishFirmware.json.devResource.SampleResource;

public class ResourcePublisher {
	
	// Connection parameters
	public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY = "f6422939005946619dd8a2575db3cb76";          // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME="json+device";								// The option to publish in device mode
	public static String CLIENT_ID="urn:lo:nsid:samples:device1";				// in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}
	
	
	//Publication parameters
	public static String TOPIC="dev/rsc";	// topic to publish to
	public static int qos = 1;              // set the qos
			
	public static void main(String[] args) {
		try {
			
			MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
			
			// create and fill you connections options
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setPassword(API_KEY.toCharArray());
			
			connOpts.setUserName(USERNAME);  // use Device Mode
			
			// now connect to LO
			sampleClient.connect(connOpts);
			System.out.println("Connected in device Mode");
			
			// Your params			
			SampleFirmware myFirmware = new SampleFirmware();
			myFirmware.v="V1.0";
			
			SampleResource resource1 = new SampleResource();
			resource1.sampleFirmware = myFirmware;
			
			LoResource loResource = new LoResource();		
			loResource.rsc = resource1;

			Gson gson = new Gson();
			String msg = gson.toJson(loResource);
			System.out.println(msg);

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
}
