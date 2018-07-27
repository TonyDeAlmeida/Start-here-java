**Mqtt in "Router Mode"**

#Objectives
These samples will demonstrate how to communicate from your device in Java in "router mode" (Bridge mode, publishing to router or consuming from router).

#Samples
You will find in this project 2 main classes illustrating :

- Publish data in Live Object : **RouterPublisher**
- Consume date from Live Objects : **RouterConsumer**

#Building the samples
The project is a maven project. You are free to use any IDE of your choice to build the project.

#Using the samples

1. Set The API_KEY and run the sample

#Publishing data
Use the RouterPublisher class. 

#Consuming data
Use the RouterConsumer class. It you start it after running the RouterPublisher class, you will notice that the messages are not delivered when running it .
If you have previously created a binding to a fifo, the messages remains in the fifo until you consume them. use the MqttFifoSample (FifoConsumer class) tio illustrate this.
