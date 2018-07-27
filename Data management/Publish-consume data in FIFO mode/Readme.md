**Mqtt in "Fifo Mode"**

#Objectives
These samples will demonstrate how to communicate from your device in Java in "fifo mode" (Bridge mode, publishing to fifo or consuming from fifo).

#Samples
You will find in this project 2 main classes illustrating :

- Publish data in Live Objects : **FifoPublisher**
- Consume date from Live Objects : **FifoConsumer**

#Building the samples
The project is a maven project. You are free to use any IDE of your choice to build the project.

#Using the samples

1. Create a fifo in Live Objects named sampleFifo
2. Set The API_KEY and run the sample

#Publishing data
Use the FifoPublisher class. 

#Consuming data
Use the FifoConsumer class. It you start it after running the FifoPublisher class, you will notice that the messages are delivered when running it (messages remains in the fifo until you consume them)
