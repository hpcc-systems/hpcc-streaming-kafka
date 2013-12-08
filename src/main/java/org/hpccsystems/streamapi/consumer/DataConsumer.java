package org.hpccsystems.streamapi.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.log4j.Logger;

public class DataConsumer {
	
	private static final Logger logger;
	private static String serverUrl;
	private static String serverConnectionTimeout;
	private static int messageListSize;
	private static String zkSyncTime;
	private static String autoCommitInterval;
	private static String consumerTimeout;
	private static String initMessage = "Not initialized";
	private static String loggerInitialized = "Logger Not Initialized";
	
	static {
		logger = Logger.getLogger(DataConsumer.class);
		if(logger != null) {
			loggerInitialized = "Logger Initialized Successfully";
			logger.info(loggerInitialized);
		}
		
		Properties props = new Properties();
		InputStream inputStream = DataConsumer.class.getResourceAsStream("DataConsumer.properties");
		if (inputStream == null) {
			initMessage = "Cannot Find the properties file DataConsumer.properties";
			logger.info(initMessage);
		} else {
			try {
				props.load(inputStream);
				serverUrl = props.getProperty("serverUrl");
				serverConnectionTimeout = props.getProperty("serverConnectionTimeout");
				messageListSize = Integer.valueOf(props.getProperty("messageListSize"));
				zkSyncTime = props.getProperty("zookeeper.sync.time.ms");
				autoCommitInterval = props.getProperty("auto.commit.interval.ms");
				consumerTimeout = props.getProperty("consumer.timeout.ms");
				initMessage = "Completed Initialization";
			} catch (IOException e) {
				logger.error(e.getMessage());
				initMessage = e.getMessage();
			}
		}
	}
	
	/**
	 * Read Messages from Apache Kafka Brokers for a particular topic. We are using Non-Blocking Consumer since we need to read only specific number of messages for a topic.
	 * messageListSize in DataConsumer.properties controls the number of messages to be fetched in one iteration.
	 * @param topic
	 * @param consumer
	 * @return Group of messages consumed.
	 */
	private static String consumeAsStream(String topic, ConsumerConnector consumer) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try {
        	while (it.hasNext() && count < messageListSize) { 
            	String message = new String(it.next().message());
            	logger.info(message);
    			sb.append(message + "\n");
    			count++;
            }
        } catch(ConsumerTimeoutException cte) {
        	// Shutdown after reading specific messages
    		consumer.shutdown();
    		logger.info(cte.getMessage());
    		return sb.toString();
        }
        
        consumer.shutdown(); // Shutdown after reading specific messages
        
        return sb.toString();
	}
	
	/**
	 * This method will be called from HPCCSystems to fetch the data from Kafka and return it back to HPCC.
	 * @param topic
	 * @param groupId
	 * @return Group of messages consumed.
	 */
	public static String consume(String topic, String groupId) {
		
		Properties props = new Properties();
		props.put("zookeeper.connect", serverUrl);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", serverConnectionTimeout);
		props.put("zookeeper.sync.time.ms", zkSyncTime);
        props.put("auto.commit.interval.ms", autoCommitInterval);
        props.put("consumer.timeout.ms", consumerTimeout);

		// Create the connection to the cluster
		ConsumerConfig consumerConfig = new ConsumerConfig(props);
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(consumerConfig);

		return consumeAsStream(topic, consumer);
	}
	
	public static String initMessage() {
		return initMessage;
	}
	
	public static String isLoggerInitialized() {
		return loggerInitialized;
	}
	
}
