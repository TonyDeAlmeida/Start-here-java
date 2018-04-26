
**State Processing**

#Objectives
These sample will demonstrate how to create state change rules and  receive state change  event fired using Live Objects state processing capabilities. 


#Samples
You will find in this project 1 main classes illustrating this sample:

- create state rule and email action  : **CreateRule**


#Building the sample
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Prepare to use the sample
1. Create a device named device1 in Live Objects or use  the auto provisioning capabilities of live objects
	

#Using the sample

 Set The API_KEY and the email to use to receive the event and run the **createRule** sample 
- you can list the matching rule, firing rule and actionPolicy created using the Swagger tool from the Live Objects environment.

- Send data from your device : If you are using the matching rule given here, you can use directly the sample from github https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data%20management/Publish%20data%20in%20device%20mode to send temperature and hygrometry values that will fire the event.
Run the sample twice : a first time with the temperature > 20° , then change the value to something less than  20°, and run it again)

- In your mailBox, you will find the email corresponding to the fired state changed

