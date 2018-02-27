package com.orange.mqttDeviceModeSample.json.devResource;

public class LoResourceUpd {
	public String id;
	public String old;
	private String newV;
	public LoM m; // info on new firmware
	public String cid;

	public String getNew() { return newV;}
	public void setNew(String newV) { this.newV = newV;}
}
