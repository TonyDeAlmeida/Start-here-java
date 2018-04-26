package com.orange.eventProcessingSample.util.json.devData;

public class ActionPolicy {

	public boolean enabled;
	public String name;
	public Trigger triggers;
	public Actions actions; // can be emailAction (this sample), httpPushAction or smsAction
	
	
}
