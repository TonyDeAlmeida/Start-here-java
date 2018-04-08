
**Event Processing**

#Objectives
These sample will demonstrate how to receive state change  event fired using Live Objects state processing capabilities. 


#Samples
You will find in this project 1 main classes illustrating this sample:

- Consume event fired by Live Object : **StateChangeConsumer**


#Building the sample
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Prepare to use the sample
1. Create a device named device1 in Live Objects or use  the auto provisioning capabilities of live objects

2. You can either create your rule by hand  using postman , or import the postman collection file  available in src/main/ressources.

###Using postman collection
1. Create the rule (you can do it by yourself (see the developper guide) or use provided postman collection in this sample. In that case :
	- Set your x-api-key in the Headers tab 
	- send the request
	- In the result panel, you should see the response of Live Objects (your id field will be different of course) :
>	{
>	    "id": "3aa53477-e463-4fc1-9214-4624a42441dc",
>	    "name": "myStateRule",
>	    "enabled": true,
>	    "stateFunction": {
>	        "if": [
>	            {
>	                "<": [
>	                    {
>	                        "var": "value.temperature"
>	                    },
>	                    20
>	                ]
>	            },
>	            "cold",
>	            {
>	                ">": [
>	                    {
>	                        "var": "value.temperature"
>	                    },
>	                    20
>	                ]
>	            },
>	            "hot"
>	        ]
>	    }
>	}
	

#Using the sample

- Set The API_KEY and run the **StateChangeConsumer** sample. Your program is now waiting for a fired event.
- Send data from your device : If you are using the matching rule given here, you can use directly the sample from github https://github.com/DatavenueLiveObjects/Start-here-java/tree/master/Data%20management/Publish%20data%20in%20device%20mode to send temperature and hygrometry values that will fire the event.
- In the console of the StateChangeConsumer you should see the event received

