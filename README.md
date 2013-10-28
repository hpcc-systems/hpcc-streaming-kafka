This repository will contain the source code/documentation for streaming data using Apache Kafka and HPCC.

Consumer:
----------

The Apache Kafka Consumer will be running on each node of the HPCC Cluster. The consumer connects to the Kafka brokers and fetches the data
depending upon the 'messageListSize' property defined in DataConsumer.properties file. We are using Non-Blocking Consumer since we need to read only specific number of messages for a topic.
The Consumers run in parallel hence the topic partition size should be equal to "Number of slave nodes". 
For e.g. If we are running a 5 node cluster (1 THOR Master and 4 slaves) then the partition size will be 4.

Producer:
----------

The are no hard and fast rules for Producer only that the partition size for the topic being produced should be equal to "Number of slave nodes".

Apache Kafka Brokers/Zookeeper:
----------

This must be configured according to message throughput and cluster availability.

Installation Steps:
----------

1. Make changes to DataConsumer.properties to point to Apache Kafka Cluster.
2. Copy DataConsumer.properties and DataConsumer.class files on each node (Including THOR Master) and add it to classpath. 
   As of now you would need to manually copy this files to each node. This will be replaced by a script that will do it for you.
3. Add the Apache Kafka jars (kafka_2.8.0-0.8.0-beta1.jar, kafka-assembly-0.8.0-beta1-deps.jar) to the classpath.
4. Add Log4j jar file to the classpath.
5. Restart the cluster.

Usage:
----------

The code base contains an example for Apache Kafka Producer (TelematicsSimulator.java) which simulates sample telematics data.
On the ECL side there are 2 schedulers:
1. DataCollection_Scheduler.ecl : Which fetched the data from Apache Kafka brokers, creates logical files and adds the logical files to superfile.
2. BuildIndex_Scheduler.ecl: Which creates a base file from the data received, creates indexes, adds the indexes to superkeys, builds a package and deploy the package to ROXIE.


Before you can start the schedulers you need to publish the queries to ROXIE (telematics_service_accdec.ecl and telematics_service_km_by_speed.ecl).

The two schedulers are independent of each other which means that if one fails the other will not be affected.

DataCollection Scheduler:
----------

Below are the high level steps that we perform for each incoming logical file:
1. The DataConsumer returns the data fetched from brokers as a string.
2. Creates a logical file for each iteration and adds it to Superfile.

BuildIndex_Scheduler:
----------

Below are the high level steps that we perform for each incoming file:
1. Swap the contents of Superfile used by DataCollection Scheduler to a temporary superfile.
2. Create a Base File which contains the cleaned/parsed data. 
3. Create a index on the sub file
4. Add the index to the SuperKey and Base file to Superfile
5. Create package XML for the queries deployed and publish the new data using packages (I do this using SOAPCALL from ECL. It can be done using ecl command line as well). 
6. Roxie Query will pick up the data from Superkey (which will be deployed using package)
7. After specific time interval (1 hour, 6 hours or 1 day) do the following:
	a.	All the sub files in a Superfile will be consolidated into a single sub file
	b.	Build one single index using the data in the superfile.
	c.	Clear up the SuperKey and add the index built in step 7(a) to the Superkey.
	d.	Clear the SuperFile. 

NOTE: Step 7 is not yet implemented and will be availble in future version.
