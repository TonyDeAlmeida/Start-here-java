**Event Processing**

#Objectives
These sample will demonstrate how to receive events fired using Live Objects event processing capabilities. 


#Samples
You will find in this project 1 main classes illustrating this sample:

- Consume event fired by Live Object : **EventConsumer**


#Building the sample
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Prepare to use the sample
1. Create a device named device1 in Live Objects or use  the auto provisionning capabilities of live objects

2. You can either create your matching and firing rule by hand  using postman , or import the postman collection file src/main/ressources/event processing starting guide.postman_collection.json  or just run the java utility **CreateRule** provided in /src/main/java/com/orange/eventProcessingSample/util

###Using postman collection
1. Create the matching and firing rule (you can do it by yourself (see the devoloppeur guide) or use provided postman collection in this sample. In taht case :
2. First run the matching rule :
	- Set your x-api-key in the Headers tab 
	- send the request
	- In the result panel, you should see the response of Live Objects (your id field will be different of course:
>		{
>		 "id": "f9090ff3-e6b3-4202-81df-974c8460f5ee",
>			    "name": "starting guide Test hygro < 20 && temp > 20",
>			    "enabled": true,
>			    "dataPredicate": {
>			        "and": [
>			            {
>			                "<": [
>			                    {
>			                        "var": "value.hygrometry"
>			                    },
>			                    20
>			                ]
>			            },
>			            {
>			                ">": [
>			                    {
>			                        "var": "value.temperature"
>			                    },
>			                    20
>			                ]
>			            }
>			        ]
>			    }
>		}

	- Open the create firing rule
	- Set your x-api-key in the Headers tab 
	- enter the id of the matching rule created before (in  the response of Live Objects)
	- send the request

>		{
>		 "id": "ffa3f316-01d7-4312-ad5d-e9630f5119c0",
>	 	"name": "evtProcessingSampleFiringRule",
>	 	"enabled": true,
>	 	"matchingRuleIds": [
>	 	"f9090ff3-e6b3-4202-81df-974c8460f5ee"
>	 	],
>	 	"aggregationKeys": [
>	 	"metadata.source"
>	 	],
>		"firingType": "ALWAYS"
>		}




#Using the sample

- Set The API_KEY and run the **EventConsumer** sample. Your program is now waiting for a fired event.
- Send data from your device : If you are using the matching rule given here, you can use directly the sample from github https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data%20management/Publish%20data%20in%20device%20mode to send temperature and hygrometry values that will fire the event.
- In the console of the EventConsumer you should see the event received :
Received message from Topic  router/~event/v1/data/eventprocessing/fired ....

