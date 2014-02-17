kafka-integration
----------

This repository contains the source code/documentation for streaming data to HPCC using Apache Kafka
via a Spring Framework (http://spring.io) based HTTP REST server.

Prerequisites:
----------

- VirtualBox - https://www.virtualbox.org
- Vagrant - http://www.vagrantup.com  

VirtualBox is a platform for running virtual machine images.
Vagrant is used to script the provisioning and running of the HPCC and Kafka VM images.

Building with Gradle:
----------
From the project root directory, execute <b>./gradlew build</b> 

The build creates a single "fat" jar file containing this project's executable code as well as all its dependencies.

Add this jar file to the classpath on the HPCC VM.

NOTE:  <b>./gradlew</b> may have DOS line endings (CR/LF) depending on your Git configuration.  
If you are on a *nix machine you may need to execute <b>dos2unix <i>gradlew</i></b> for the build to execute.

Provisioning and Running the Virtual Machines
----------
Make sure VirtualBox and Vagrant are installed (see Prerequisites).

Open a command line window and navigate to the root directory of this project.

Type <b>vagrant up --provision</b> to download, provision, and start the HPCC and Kafka
virtual machines.  The first time this step is run it will take several minutes to download
the needed files.
 
Data Consumer Installation - HPCC Nodes:
----------
- Copy <b>build/libs/kafka-integration-n.n.n.jar (created by the Gradle build) to each HPCC node 
(including the THOR Master) and add it to the classpath.  (TODO automate this step)
- Restart the HPCC cluster.

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
	<ol> 
		<li> All the sub files in a Superfile will be consolidated into a single sub file </li>
		<li> Build one single index using the data in the superfile. </li>
		<li> Clear up the SuperKey and add the index built in step 7(i) to the Superkey. </li>
	    <li> Clear the SuperFile. </li>
	</ol>
</ol>
<b>NOTE:</b> Step 7 is not yet implemented and will be available in a future version.<br />
