**Mqtt in "Device Mode" : update firmware**

#Objectives
These samples will demonstrate how to communicate from your device in Java in "Device mode" to update the firwmare of your device to a new version
This mode allows you to manage the device using Live Objects


#Sample
You will find in this project a main classes illustrating this sample:

- Download a new firmware : **RessourceUpdater**

#Building the samples
The project is a maven project. You are free to use any IDE of your choice to build the project.


#Using the samples

1. First run the MqttDeviceModePublishFirmware sample
2 in the Firmwares list in Live Objects, add de new version V1.1 to the sampleFirmware. Add a binary file of your choice (may be a jpeg picture for example)
3. In Live Objects change the device1 firmware version to V1.1 
2. Set The API_KEY and run the sample
3. Check that you have downloaded the binary file that you have provided for the V1.1 version.

