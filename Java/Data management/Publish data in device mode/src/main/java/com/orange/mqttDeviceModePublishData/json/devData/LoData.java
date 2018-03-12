package com.orange.mqttDeviceModePublishData.json.devData;

public class LoData {
	public String s; 		// fill it  to persist the data in dataZone
	public String ts;		// your timestamp, if null : filled by LiveObject
	public String m;		// fill if you want your data to be indexed par Elastic Search
	public double[] loc;	// location of your device if known
	public SampleData v;	// your own json structure
	public String t;		// optional tags regarding your needs
	
}
