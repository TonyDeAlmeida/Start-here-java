This document describes how to use the main features of Live Objects to develop your IoT application.



# Getting Started Guide #

This document describes how to use the main features of Live Objects (LO) to develop your IoT application. 

## Terminology: ##

* client: your application that will make Live Objects API calls
* API-KEY: the token that allows you to connect to your own tenant/space in Live Objects
* CLIENT-ID: identifier of the device or of the application currently trying to connect to Live Objects
* endpoint: the URL to access to a function of the API in REST
* header: a user-defined HTTP header to carry information in a REST call


## Before starting ## 

We focus here on the use of the APIs to push and consume data from the “Live Objects Bus” and to perform “device management” operations. 
If you are familiar with the concepts of Live Objects bus (topics, publish mode...), you can skip this chapter.


## 1.1 LIVE OBJECTS BUS ## 

What is Live Objects BUS ? It’s basically a place where you can push information to and from where you can consume this information.
In the IOT world, anybody will have in mind a sample case:

```ruby
Device  : publish data -> Live Objects -> consume by application(s)
```

But you can also configure your device entering data in the web portal:
```ruby
Enter data in Live Objects UI -> consume by your device
```

Publishing or consuming any kind of information, the first relevant question is:

1. How can I do that ?  With Live Objects you will use different protocols, at this time MQTT(S) or HTTP(S)/Rest APIs are available to talk with Live Objects BUS. Choosing the right protocol depends on the Live Objects functionality you want to use.

2. How can I address the bus ? You only need a little information
    a. The server address
    b. A “message box address”. In our case, these message boxes are in fact message queues (messages are queued in them), and we will use the name of “Topic” as the address of these message queues.
    
3. How am I identified ? You will have to use:
    * your own API-KEY
    * a CLIENT-ID
 
## 1.2 TOPIC concepts ## 

We can make a parallel. If you want to send somebody some information, you will need:
    * his country, his town, his street, his street address, his stage, his door number

A Topic is somehow any of this kind of information.
    * country: all people in the country will be able to receive your message
    * country/town/street: all people of the street of the town will be able to receive your message
    * …
    * country/town/street/.../door: only the people living there will be able to receive your message.


In Live Objects, you have predefined parts of the addresses that are fixed, the other are of your choice.


## 1.3 DEVICE MODE VS BRIDGE MODE ## 

You can publish using 2 modes:

Type mode | what for ?
------------ | -------------
Device mode  | the mode for devices to publish directly to Live Objects.</td>
Bridge mode | used when you are using a gateway between your devices and Live Objects.

When using one mode, you must follow the following rules:
The first part of the topic is the type of the topic: pubsub/, fifo/ or router/ for the bridge mode
and dev/ for the “device mode”.
Pay attention that only some addresses will make your data persisting in the Live Objects “data zone”, all others will be available only for your consumers (Elastic search/Kibana impact)


Publish mode  | Type | Address | Persisted in data zone
------------ | ------------- | ------------ | -------------
Device Mode | dev/ | fixed: data, cfg, ... | yes for dev/data , yes for dev/data/xxx
Bridge mode (pubsub) | pubsub/ | - | no
Bridge mode (fifo) | fifo/ | at your choice | no
Bridge mode (router) | router/ | at your choice | no
Bridge mode (router) | router/ | ~event/v1/data/new, ~event/v1/data/new/xxx | yes

### 1 Publish data ###
Device mode ? Bridge mode ? How to choose:

    * Device mode is the best mode for device publishing directly to Live Objects
    * Bridge mode is more used when you are using a gateway that hides Live Objects to your device.

### 2 Consume data ###
Live Objects has a very powerful concept: the fifos !
Fifos allow you to consume data from your application, even when your application is down for any reason (of course, while it is down you will not consume anything … , but as soon as your application is up again, all data published in the mean time will be delivered in the right order to your application.
In pubsub mode or router mode, your application has to be up to consume the data at the time they are published to Live Objects

### 3 The big question: topic, fifo and bindings ###
Well: how do I choose a topic to publish to ?, how does this influence the way data will be consumed ?

## 3.1 Mqtt devices ## 
Data published on topic <b>/dev/data/xxx</b>  (xxx may be what you want) can be consumed on a fifo created with routing key <b>~event.v1.data.new</b>

But  perhaps you need  to route your data to different consumer to best suit your needs. Let’s take an example:
I want to consume the data of some devices to a development environment while some others are used in production.
In that case you can publish and consume your data the following way:
You create 2 fifos, let’s say for example devFifo and prodFifo.
Configure your production application to consume from prodFifo, while your development application will consume from devFifo.
Configure your devices so that devices in production mode and devices used for development will publish to different topics (let’s say for example /dev/data/myprod and /dev/data/mydev.
Then just create two routing keys in Live Objects for your two fifos:

    * prodFifo : ~event.v1.data.new.myprod
    * devFifo : ~event.v1.data.new.mydev

Device kind | Topic to publish | Fifo | &nbsp;
------------ | ------------ | ------------ | ------------
production | /dev/data/myprod | prodFifo | ~event.v1.data.new.myprod
development | /dev/data/mydev | devFifo | ~event.v1.data.new.mydev


## 3.2 Lora devices ##
Data sent by Lora devices are automatically available in Live Objects data zone. You only have to decide of the way to consume them.
Best way to take benefit of the Fifo mode: create fifos in Live Objects UI, with a binding rule:

    * ~event.v1.data.new.urn.lora.# : (deprecated and will be decommissioned in december 2018) all messages of all devices
    * ~event.v1.data.new.{DevEUI}.# : (deprecated and will be decommissioned in december 2018) all messages of the device DevEUI
    * ~event.v1.data.new.typ.*.dev.*.con.lora.evt.*.grp.# : all messages of all devices
    * ~event.v1.data.new.typ.*.dev.{deviceId}.con.lora.evt.*.grp.# : all messages of the device, deviceId is the URN of the device: urn:lo:nsid:lora:{devEUI}


# 4 Samples introduction # 

All samples are independent project that you run according your needs.


## 4.1 JAVA SAMPLES (PUBLISHING AND CONSUMING) ## 

All projects are maven projects, allowing you to use them directly in your preferred IDE.
They are all based on the same structure, and use the paho-mqtt java library.


### 4.1.1 LIVE OBJECTS PARAMETERS ###

Use static constants for all Live Objects parameters. This will certainly not what you will prefer to do in your real application code, but centralizing them in the samples gives you a complete overview of the different parameters.
A first group of parameters defines the connection to Live Objects

```ruby
// Connection parameters
public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
public static String API_KEY = "<<YOUR API KEY>>";                             // <-- REPLACE by YOUR API_KEY! 
public static String USERNAME="json+device";                                // The option to publish in device mode
public static String CLIENT_ID="urn:lo:nsid:samples:device1";               // in device mode: urn:lo:nsid:{namespace}:{id}
```

Notice that you have to define the connection mode (device mode or bridged mode) when connecting  
Connecting using SSL : you only have to change the SERVER end point to:

```ruby
public static String SERVER mqtts://liveobjects.orange-business.comm:8883
```

The second group of parameters defines the way your message will be published to Live Objects:


```ruby
//Publication parameters
public static String TOPIC="dev/data";  // topic to publish to
public static int qos = 1;              // set the qos
```

### 4.1.2 JSON STRUCTURE ###

All JSON structures are defined in separate packages.
All JSON structures defined by Live Objects are named the same way: they start with Lo (LoCfg, LoData…). The other ones are your structure that you can change according your needs.

# 5 USING MQTT(S) SAMPLES #

## 5.1 LIST OF SAMPLES ##

For each project listed below, you will find more information in the readme.md

Mode | Project name
------------ | -------------
Device Mode |  [Publish data in device mode] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data%20management/Publish%20data%20in%20device%20mode)  <br> [Commands] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Device management/MQTT devices/Commands)    <br>   [Configuration] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Device management/MQTT devices/Configuration)  <br> [Publishing info] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Device management/MQTT devices/Publishing infos) 
Bridge Mode / pubSub | [Publish-Consume data in PubSub mode] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data management/Publish-Consume data in PubSub mode)
Bridge Mode / router | [Publish-consume data in router mode] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data management/Publish-consume data in router mode)  |  
Bridge Mode / fifo | [Publish-consume data in FIFO mode] (https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data management/Publish-consume data in FIFO mode)


## 5.2 PUBLISHING DATA FROM LIVE OBJECTS IN JAVA ##



Once defined the required parameters (remember upper in the document), the way to publish is always the same:

<u>Create your client</u>

```ruby
MqttClient sampleClient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
```

<u>Create and fill options</u>

```ruby
qttConnectOptions connOpts = new MqttConnectOptions();
connOpts.setCleanSession(true);
connOpts.setPassword(API_KEY.toCharArray());    
connOpts.setUserName(USERNAME);  // use Device Mode
```

<u>Connect to Live Objects</u>

```ruby
sampleClient.connect(connOpts);
```

<u>Create your message</u>

```ruby
LoData loData = new LoData();
... // fill members in the JSON structure (see reference guide for the JSON to use according to the publish mode and the topic)
```

<u>Send your messages</u>

```ruby
MqttMessage message = new MqttMessage(msg.getBytes());
message.setQos(qos);
sampleClient.publish(TOPIC, message);
```

<u>Disconnect</u>

```ruby
sampleClient.disconnect();
```

## 5.3 CONSUMING DATA FROM LIVE OBJECTS ##

Once defined the required parameters (remember upper in the document), the way to consume is always the same:
The main difference with publishing is: you have to create a callback class dedicated to the processing of the delivery of messages, and register in an instance of it in your mqttClient.

### 5.3.1 With MQTT ###
<u>Create your client</u>

```ruby
MqttClient sampleCLient = new MqttClient(SERVER, CLIENT_ID, new MemoryPersistence());
```

<u>Register the callback class that will handle subscription and message processing</u>

```ruby
sampleClient.setCallback(new SimpleMqttCallback(sampleClient); // SEE BELOW !!
```

<u>Create and fill options</u>

```ruby
MqttConnectOptions connOpts = new MqttConnectOptions();
connOpts.setCleanSession(true);
connOpts.setPassword(API_KEY.toCharArray());    
connOpts.setUserName(USERNAME);  // use Bridge mode here to consume from fifo or router !
connOpts.setAutomaticReconnect(true);
```

<u>Connect to Live Objects</u>

```ruby
sampleClient.connect(connOpts);
```

<u>Wait for messages processed by your SimpleMqttCallback object</u>

```ruby
synchronized (sampleClient) {
sampleClient.wait();
}
```

<u>Disconnect</u>

```ruby
sampleClient.disconnect();
```

<u>Now, have a look at the the callback class :</u>
This class should implement the MqttCallbackExtended interface:
First create the constructor:

```ruby
private MqttClient mqttClient;
public SimpleMqttCallback(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
}
```

You need the reference to your mqttClient previously created to subscribe to the right Topic
Then fill the different methods you implement

```ruby
public void connectComplete(boolean reconnect, String serverURI) {
```

Once your MqttClient is connected, you will subscribe here:

```ruby
try {
System.out.printf("Consuming from topic : ", TOPIC);
    mqttClient.subscribe(TOPIC);
    System.out.println("... subscribed.");                              
} catch (MqttException e) {
    System.out.println("Error during subscription");
}   

public void messageArrived(String topic, MqttMessage message) throws Exception {
All messages will be processed, (here simple print on stdout, but of course, you can process the messages according to your needs !
System.out.println("Message received from Topic  " + TOPIC + " : " + mqttMessage);
}
```


The 2 last methods can be left empty. Use them according your needs (see Mqtt library documentation)


```ruby
public void connectionLost(Throwable cause) {
}
public void deliveryComplete(IMqttDeliveryToken token) {
}
```
