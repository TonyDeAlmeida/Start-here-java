package com.orange.mqttDeviceModeSample.json.devCmd;

public class LoCmd {
	private String req;
	private Params arg;
	private int cid;
	
	public String getReq() {
		return req;
	}
	public void setReq(String req) {
		this.req = req;
	}
	public Params getArg() {
		return arg;
	}
	public void setArg(Params arg) {
		this.arg = arg;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
}
