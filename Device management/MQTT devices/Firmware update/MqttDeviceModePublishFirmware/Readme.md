**Mqtt in "Device Mode" : publish firmware**

#Objectives
These sample will demonstrate how to communicate from your device in Java in "Device mode" to publish firmware information about your device in Live Objects.


#Samples
You will find in this project a main classes illustrating this sample:

- Publish firmware and version of  your device in Live Objects : **ResourcePublisher**


#Building the sample
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Using the sample

1. Create a firmware with id "sampleFirmware" in Live Objects Firmwares list. add the V1.0 version
2. Create a device named device1 in Live Objects or use the auto provisioning capabilities of live objects
3. Set The API_KEY and run the sample
4. Check the result in Live Objects :
	firmware id=sampleFirmware, version=V1.0
	
#next step
You can use the **MqttDeviceModeUpdateFirmware** project to see how to update the firmware of your device using Live Objects
