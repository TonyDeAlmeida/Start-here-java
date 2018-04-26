**Event Processing**

#Objectives
These sample will demonstrate how to receive events and send email using Live Objects event processing and "event to action" capabilities. 


#Samples
You will find in this project 2 main classes illustrating this sample:

- create the rules and the email action : **CreateRule**
- Consume event fired by Live Object : **EventConsumer**


#Building the sample
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Prepare to use the sample
1. Create a device named device1 in Live Objects or use  the auto provisioning capabilities of live objects

2. You can either create your matching , firing rule and event to action by hand  using postman , or just run the java utility **CreateRule** provided.

##Using postman collection
Create the matching, firing rule and actionPolicy (you can do it by yourself (see the developer guide) 


##Using the sample

- Set The API_KEY and the email to use to receive the event and run the **createRule** sample 
- you can list the matching rule, firing rule and actionPolicy created using the Swagger tool from the Live Objects environment.

- To consume the event fired, use the **EventConsumer** sample. Your program is now waiting for a fired event.

- Send data from your device : If you are using the matching rule given here, you can use directly the sample from github https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data%20management/Publish%20data%20in%20device%20mode to send temperature and hygrometry values that will fire the event.

- In the console of the EventConsumer you should see the event received :
Received message from Topic  router/~event/v1/data/eventprocessing/fired ....
- In your mailBox, you will find the email corresponding to the fired event

