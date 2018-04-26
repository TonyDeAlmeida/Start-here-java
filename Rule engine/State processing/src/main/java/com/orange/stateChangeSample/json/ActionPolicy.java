package com.orange.stateChangeSample.json;

public class ActionPolicy {

	public boolean enabled;
	public String name;
	public Trigger triggers;
	public Actions actions; // can be emailAction (this sample), httpPushAction or smsAction
	
	
}
