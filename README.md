<p>This document describes how to use the main features of Live Objects to develop your IoT application.</p>



<h1 id="prerequisite">Getting Started Guide</h1>

<p>This document describes how to use the main features of Live Objects (LO) to develop your IoT application. You will find all the needed information about the following points :</p>

<ul>
<li><a anchor="" href="https://developer.orange.com/apis/datavenue/getting-started#" target="_self">bla bla</a></li>
<li><a anchor="" href="https://developer.orange.com/apis/datavenue/getting-started#" target="_self">bla bla</a></li>
</ul>


<h2 id="terminology">Terminology:</h2>
<ul>
<li>client: your application that will make Live Objects API calls</li>
<li>API-KEYS: the token that allows you to connect to your own tenant/space in Live Objects</li>
<li>CLIENT-ID: identifier of the device or of the application currently trying to connect to Live Objects.</li>
<li>endpoint: the URL to access to a function of the API in REST</li>
<li>header: a user-defined HTTP header to carry information in a REST call</li>
</ul>

<h2 id="before-starting">Before starting</h2>

<p>We focus here on the use of the APIs to push and consume data from the “Live Objects Bus” and to perform “device management” operations. </p><p>
If you are familiar with the concepts of Live Objects bus (topics, publish mode...), you can skip this chapter.
</p>

<h3 id="live-objects-bus">1.1 LIVE OBJECTS BUS</h3>



<p>What is Live Objects BUS ? It’s basically a place where you can push information to and from where you can consume this information.</p>
<p>In the IOT world, anybody will have in mind a sample case : </p>
```ruby
Device  : publish data -> Live Objects -> consume by application(s)
```

<p>But you can also configure your device entering data in the web portal :</p>
```ruby
Enter data in Live Objects UI -> consume by your device
```

<p>Publishing or consuming any kind of information, the first relevant question is :</p>
<ul>
<li type="1">How can I do that ?  With Live Objects you will use different protocols, at this time MQTT(S) or HTTP(S)/Rest APIs are available to talk with Live Objects BUS. Choosing the right protocol depends of the Live Objects functionality you want to use.</li>
<li type="1">How addressing the bus ? You only need a little information</li>
    <ul>
    <li type="a" value="1">The server address</li>
    <li type="a">A “message box address”. In our case, these message boxes are in fact message queues (messages are queued in them), and we will use the name of “Topic” as the address of these message queues.</li>
    </ul>
<li type="1">How am I identified ? You will have to use:</li>
    <ul>
    <li>your own API-KEY</li>
    <li>a CLIENT-ID</li>
    </ul>
</ul>

<h3 id="topic-concept">1.2 TOPIC concepts</h3>

<p>We can make a parallel. If you want to send somebody some information, you will need:</p>
<ul>
<li>its country, its town, its street, its street address, its stage, its door number</li>
</ul>

A Topic is somehow any of this kind of information.
<ul>
<li>country : all people in the country will be able to receive your message</li>
<li>country/town/ street ; all people of the street of the town will be able to receive your message</li>
<li>…</li>
<li>country/town/street/.../door : only the people living there will be able to receive your message.</li>
</ul>
<p>In Live Objects, you have predefined parts of the addresses that are fixed, the other are of your choice.</p>


<h3 id="device-mode-vs-bridge-mode">1.3 DEVICE MODE VS BRIDGE MODE</h3>

You can publish using 2 modes:
<table>
<tr>
    <td>Device mode</td>
    <td>the mode for devices to publish directly to Live Objects.</td>
</tr>
<tr>
    <td>Bridge mode</td>
    <td>used when you are using a gateway between your devices and Live Objects.</td>
</tr>
</table>

<p>When using one mode, you must follow the following rules:</p>
<p>The first part of the topic is the type of the topic: pubsub/, fifo/ or router/ for the bridge mode
and dev/ for the “device mode”.</p>
<p>Pay attention that only some addresses will make your data persisting in the Live Objects “data zone”, all others will be available only for your consumers (Elastic search/Kibana impact)</p>

<table>
<tr>
    <td>Publish mode</td>
    <td>Type</td>
    <td>Address</td>
    <td>Persisted in data zone</td>
</tr>
<tr>
    <td>Device Mode</td>
    <td>dev/</td>
    <td>fixed : data, cfg, ...</td>
    <td>yes for dev/data<br>
    yes for dev/data/xxx
    </td>
</tr>
<tr>
    <td>Bridge mode (pubsub)</td>
    <td>pubsub/</td>
    <td></td>
    <td>no</td>
</tr>
<tr>
    <td>Bridge mode (fifo)</td>
    <td>fifo/</td>
    <td>at your choice</td>
    <td>no</td>
</tr>
<tr>
    <td>Bridge mode (routeur)</td>
    <td>routeur/</td>
    <td>at your choice</td>
    <td>no</td>
</tr>
<tr>
    <td>Bridge mode (routeur)</td>
    <td>routeur/</td>
    <td>~event/v1/data/new<br>
~event/v1/data/new/xxx</td>
    <td>yes</td>
</tr>
</table>

<h2 id="samples-introduction">Samples introduction</h2>

<p>All samples are independent project that you run according your needs.</p>


<h3 id="java-samples">2.1 JAVA SAMPLES (PUBLISHING AND CONSUMING)</h3>

<p>All projects are maven projects, allowing you to use them directly in your preferred IDE.
They are all based on the same structure, and use the paho-mqtt java library.</p>


<h4 id="live-objects-parameters">2.1.1 LIVE OBJECTS PARAMETERS</h4>

<p>Use static constants for all Live Objects parameters. This will certainly not what you will prefer to do in your real application code, but centralizing them in the samples gives you a complete overview of the different parameters.</p><p>
A first group of parameters defines the connection to Live Objets</p>

<pre><code type="java">// Connection parameters
public static String SERVER = "tcp://liveobjects.orange-business.com:1883"; // declare Live Objects end point
public static String API_KEY = "<<YOUR API KEY>>";                             // <-- REPLACE by YOUR API_KEY! 
public static String USERNAME="json+device";                                // The option to publish in device mode
public static String CLIENT_ID="urn:lo:nsid:samples:device1";               // in device mode : urn:lo:nsid:{namespace}:{id}
</code></pre>

<p>Notice that you have to define the connection mode (device mode or bridged mode) when connecting</p>  
<p>Connecting using SSL : you only have to change the SERVER end point to:
<code>public static String SERVER mqtts://liveobjects.orange-business.comm:8883</code></p>

<p>The second group of parameters defines the way your message will be published to Live Objects:</p>

<pre><code>//Publication parameters
public static String TOPIC="dev/data";  // topic to publish to
public static int qos = 1;              // set the qos
</code></pre>

<h4 id="json-structure">2.1.2 JSON STRUCTURE</h4>

<p>All JSON structures are defined in separate packages.</p>
<p>All JSON structures defined by Live Objects are named the same way: they start with Lo (LoCfg, LoData…). The other ones are your structure that you can change according your needs.</p>

<h2 id="using-mqtt-samples">USING MQTT(S) SAMPLES</h2>

<h3 id="list-samples">3.1 LIST OF SAMPLES</h3>

<p>For each project listed below, you will find more information in the readme.md</p>

<table>
    <tr>
        <td>Mode</td>
        <td>Project name</td>
    </tr>
        <tr>
        <td>Device Mode</td>
        <td><a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Data%20management/Publish%20data%20in%20device%20mode">Publish data in device mode</a><br>
        <a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Device management/MQTT devices/Commands">Commands</a><br>   
        <a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Device management/MQTT devices/Configuration">Configuration</a><br>         
        <a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Device management/MQTT devices/Publishing infos">Publishing info</a><br>          
</td>
    </tr>
    <tr>
        <td>Bridge Mode / pubSub
        </td>
        <td><a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Data management/Publish-Consume data in PubSub mode">Publish-Consume data in PubSub mode</a></td>
    </tr>
    <tr>
        <td>Bridge Mode / router</td>
        <td><a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Data management/Publish-consume data in router mode">Publish-consume data in router mode</a></td>
    </tr>
    <tr>
        <td>Bridge Mode / fifo</td>
        <td><a href="https://github.com/DatavenueLiveObjects/Start-here/tree/master/Java/Data management/Publish-consume data in FIFO mode">Publish-consume data in FIFO mode</a></td>
    </tr>
</table>


<h3 id="publishing-data">3.2 PUBLISHING DATA FROM LIVE OBJECTS IN JAVA </h3>



<p>Once defined the required parameters (remember upper in the document), the way to publish is always the same :</p>

<p><u>Create your client</u></p>

<pre><code>MqttClient sampleClient = new MqttClient(<b>SERVER, CLIENT_ID</b>, new MemoryPersistence());
</code></pre>

<p><u>Create and fill options</u></p>

<pre><code>qttConnectOptions connOpts = new MqttConnectOptions();
connOpts.setCleanSession(true);
connOpts.setPassword(<b>API_KEY</b>.toCharArray());    
connOpts.setUserName(<b>USERNAME</b>);  // use Device Mode
</code></pre>

<p><u>Connect to Live Objects</u></p>

<pre><code>sampleClient.connect(connOpts);
</code></pre>

<p><u>Create your message</u></p>

<pre><code>LoData loData = new LoData();
... // fill members in the JSON structure (see reference guide for the JSON to use according to the publish mode and the topic)
</code></pre>

<p><u>Send your messages</u></p>

<pre><code>MqttMessage message = new MqttMessage(msg.getBytes());
message.setQos(qos);
sampleClient.publish(<b>TOPIC</b>, message);
</code></pre>

<p><u>Disconnect</u></p>

<pre><code>sampleClient.disconnect();
</code></pre>


<h3 id="consuming-data">3.3 CONSUMING DATA FROM LIVE OBJECTS</h3>

<p>Once defined the required parameters (remember upper in the document), the way to consume is always the same :</p>
<p>The main difference with publishing is : you have to create a callback class dedicated to the processing of the delivery of messages, and register in an instance of it in your mqttClient.</p>

<h4 id="consuming-data-mqtt">3.3.1 With MQTT</h4>
<p><u>Create your client</u></p>

<pre><code>MqttClient sampleCLient = new MqttClient(<b>SERVER</b>, <b>CLIENT_ID</b>, new MemoryPersistence());
</code></pre>

<p><u>Register the callback class that will handle subscription and message processing</u></p>

<pre><code>sampleClient.setCallback(new SimpleMqttCallback(sampleClient); // SEE BELOW !!
</code></pre>

<p><u>Create and fill options</u></p>

<pre><code>MqttConnectOptions connOpts = new MqttConnectOptions();
connOpts.setCleanSession(true);
connOpts.setPassword(<b>API_KEY</b>.toCharArray());    
connOpts.setUserName(<b>USERNAME</b>);  // use Bridge mode here to consume from fifo or router !
connOpts.setAutomaticReconnect(true);
</code></pre>

<p><u>Connect to Live Objects</u></p>

<pre><code>sampleClient.connect(connOpts);
</code></pre>

<p><u>Wait for messages processed by your SimpleMqttCallback object</u></p>

<pre><code>synchronized (sampleClient) {
sampleClient.wait();
}
</code></pre>

<p><u>Disconnect</u></p>

<pre><code>sampleClient.disconnect();
</code></pre>

<p><u>Now, have a look at the the callback class :</u></p>
<p>This class should implement the MqttCallbackExtended interface :</p>
<p>First create the constructor :</p>

<pre><code>private MqttClient mqttClient;
public SimpleMqttCallback(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
}
</code></pre>

<p>You need the reference to your mqqClient previously created to subscribe to the right Topic</p>
<p>Then fill the different methods you implement</p>

<pre><code>public void connectComplete(boolean reconnect, String serverURI) {
</code></pre>

<p>Once your MqttClient is connected, you will subscribe here : </p>

<pre><code>try {
System.out.printf("Consuming from topic : ", <b>TOPIC</b>);
    mqttClient.subscribe(<b>TOPIC</b>);
    System.out.println("... subscribed.");                              
} catch (MqttException e) {
    System.out.println("Error during subscription");
}   
</code></pre>

<pre><code>public void messageArrived(String topic, MqttMessage message) throws Exception {
All messages will be processed, (here simple print on stdout, but of course, you can process the messages according to your needs !
System.out.println("Message received from Topic  " + TOPIC + " : " + mqttMessage);
}
</code></pre>

<p>
The 2 last methods can be left empty. Use them according your needs (see Mqtt library documentation)
</p>

<pre><code>public void connectionLost(Throwable cause) {
}
public void deliveryComplete(IMqttDeliveryToken token) {
}
</code></pre>


<h4 id="consuming-data-lora">3.3.2 With LoRa</h4>

<p>Data sent by LoRa devices are automatically available in Live Objects datazone. You will consume them the same way as MQTT messages published directly from your « MQTT devices ». You only have to decide the way to consume them.</p>

<p>Best way : create a fifo in Live Objects portal, with a binding rule :</p>
<ul>
    <li>~event.v1.data.new.urn.lora.# : all messages from all devices</li>
    <li>~event.v1.data.new.{DevEUI}.# : all messages from the DevEUI device.</li>
</ul>


<h2 id="using-rest">USING REST API IN JAVA</h2>
<p>The best way to become familiarized with the REST Api(s) is to see them in action directly with Live Objects.</p>


<h3 id="">4.1   JAVA REST SAMPLE</h3>

<p>We give you here a sample, using Jersey</p>






