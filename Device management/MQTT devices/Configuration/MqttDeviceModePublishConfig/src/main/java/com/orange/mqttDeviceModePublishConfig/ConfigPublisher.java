package com.orange.mqttDeviceModePublishConfig;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.Gson;
import com.orange.mqttDeviceModePublishConfig.json.devCfg.Cfg;
import com.orange.mqttDeviceModePublishConfig.json.devCfg.LoCfg;
import com.orange.mqttDeviceModePublishConfig.json.devCfg.Param1;
import com.orange.mqttDeviceModePublishConfig.json.devCfg.Param2;

/**
 * This sample will publish configuration information for a device : device1
 * At this end you can take a look in the data zone of live objects to see the data sent.
 * You may also, when familiar with the concepts create a fifo (BEFOER SENDING DATA !!)
 * and consume the data sent form this fifo
 **/

public class ConfigPublisher {
	
	// Connection parameters
	public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
	public static String API_KEY = "enter your api key here";          // <-- REPLACE by YOUR API_KEY!	
	public static String USERNAME="json+device";								// The option to publish in device mode
	public static String CLIENT_ID="urn:lo:nsid:samples:device1";				// in device mode : should be the syntax urn:lo:nsid:{namespace}:{id}
	
	
	//Publication parameters
	public static String TOPIC="dev/cfg";	// topic to publish to
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
			Param1 myParam1 = new Param1();
			Param2 myParam2 = new Param2();

			myParam1.t="i32";
			myParam1.v=2450;
			
			myParam2.t="str";
			myParam2.v="stringValue1";
			
			Cfg cfg = new Cfg();		
			cfg.param1 = myParam1;
			cfg.param2 = myParam2;
			
			// in Lo structure
			LoCfg loCfg = new LoCfg();
			loCfg.cfg = cfg;
			
			Gson gson = new Gson();
			String msg = gson.toJson(loCfg);
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
