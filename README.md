kafka-integration
----------

This repository will contain the source code/documentation for streaming data using Apache Kafka and HPCC.

Consumer:
----------

The Apache Kafka Consumer will be running on each node of the HPCC Cluster. The consumer connects to the Kafka brokers and fetches the data
depending upon the 'messageListSize' property defined in DataConsumer.properties file. We are using Non-Blocking Consumer since we need to read only specific number of messages for a topic.
The Consumers run in parallel hence the topic partition size should be equal to "Number of slave nodes". <br />
For e.g. If you are running a 5 node cluster (1 THOR Master and 4 slaves) then the partition size will be 4.

Producer:
----------

The are no hard and fast rules for Producer only that the partition size for the topic being produced should be equal to "Number of slave nodes".

Apache Kafka Brokers/Zookeeper:
----------

This must be configured according to message throughput and cluster availability.

Installation Steps:
----------

- Make changes to DataConsumer.properties to point to Apache Kafka Cluster.
- Copy DataConsumer.properties and DataConsumer.class files on each node (Including THOR Master) and add it to classpath. 
  As of now you would need to manually copy this files to each node. This will be replaced by a script that will do it for you.
- Add the Apache Kafka jars (kafka_2.8.0-0.8.0-beta1.jar, kafka-assembly-0.8.0-beta1-deps.jar) to the classpath.
- Add Log4j jar file to the classpath.
- Restart the cluster.

Usage:
----------

The code base contains an example for Apache Kafka Producer (TelematicsSimulator.java) which simulates sample telematics data.
On the ECL side there are 2 schedulers: <br />
- DataCollection_Scheduler.ecl : Which fetched the data from Apache Kafka brokers, creates logical files and adds the logical files to superfile.
- BuildIndex_Scheduler.ecl: Which creates a base file from the data received, creates indexes, adds the indexes to superkeys, builds a package and deploy the package to ROXIE.


Before you can start the schedulers you need to publish the queries to ROXIE (telematics_service_accdec.ecl and telematics_service_km_by_speed.ecl).

The two schedulers are independent of each other which means that if one fails the other will not be affected.

DataCollection Scheduler:
----------

Below are the high level steps that we perform for each incoming logical file: <br />
1. The DataConsumer returns the data fetched from brokers as a string. <br />
2. Creates a logical file for each iteration and adds it to Superfile. <br />

BuildIndex_Scheduler:
----------

Below are the high level steps that we perform for each incoming file: <br />
<ol>
<li> Swap the contents of Superfile used by DataCollection Scheduler to a temporary superfile. </li>
<li> Create a Base File which contains the cleaned/parsed data. </li>
<li> Create a index on the sub file </li>
<li> Add the index to the SuperKey and Base file to Superfile </li>
<li> Create package XML for the queries deployed and publish the new data using packages (I do this using SOAPCALL from ECL. It can be done using ecl command line as well). </li>
<li> Roxie Query will pick up the data from Superkey (which will be deployed using package) </li>
<li> After specific time interval (1 hour, 6 hours or 1 day) do the following: </li>
<li> - All the sub files in a Superfile will be consolidated into a single sub file </li>
	 - Build one single index using the data in the superfile.
	 - Clear up the SuperKey and add the index built in step 7(a) to the Superkey.
	 - Clear the SuperFile.
</ol>
NOTE: Step 7 is not yet implemented and will be availble in future version.
