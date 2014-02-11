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

    private final Logger logger;
    private String       serverUrl;
    private String       serverConnectionTimeout;
    private int          messageListSize;
    private String       zkSyncTime;
    private String       autoCommitInterval;
    private String       consumerTimeout;
    private String       initMessage       = "Not initialized";
    private String       loggerInitialized = "Logger Not Initialized";
    
    public DataConsumer() {
        this.logger = Logger.getLogger(DataConsumer.class);
        if (this.logger != null) {
            this.loggerInitialized = "Logger Initialized Successfully";
            this.logger.info(this.loggerInitialized);
        }

        final Properties props = new Properties();
        final InputStream inputStream =
                DataConsumer.class.getResourceAsStream("DataConsumer.properties");
        if (inputStream == null) {
            this.initMessage = "Cannot Find the properties file DataConsumer.properties";
            this.logger.info(this.initMessage);
        } else {
            try {
                props.load(inputStream);
                this.serverUrl = props.getProperty("serverUrl");
                this.serverConnectionTimeout =
                        props.getProperty("serverConnectionTimeout");
                this.messageListSize =
                        Integer.valueOf(props.getProperty("messageListSize"));
                this.zkSyncTime = props.getProperty("zookeeper.sync.time.ms");
                this.autoCommitInterval = props.getProperty("auto.commit.interval.ms");
                this.consumerTimeout = props.getProperty("consumer.timeout.ms");
                this.initMessage = "Completed Initialization";
            } catch (final IOException e) {
                this.logger.error(e.getMessage());
                this.initMessage = e.getMessage();
            }
        }
    }

    /**
     * This method will be called from HPCCSystems to fetch the data from Kafka and return
     * it back to HPCC.
     * 
     * @param topic
     * @param groupId
     * @return Group of messages consumed.
     */
    public String consume(final String topic, final String groupId) {

        final Properties props = new Properties();
        props.put("zookeeper.connect", this.serverUrl);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", this.serverConnectionTimeout);
        props.put("zookeeper.sync.time.ms", this.zkSyncTime);
        props.put("auto.commit.interval.ms", this.autoCommitInterval);
        props.put("consumer.timeout.ms", this.consumerTimeout);

        // Create the connection to the cluster
        final ConsumerConfig consumerConfig = new ConsumerConfig(props);
        final ConsumerConnector consumer =
                Consumer.createJavaConsumerConnector(consumerConfig);

        return consumeAsStream(topic, consumer);
    }

    public String initMessage() {
        return this.initMessage;
    }

    public String isLoggerInitialized() {
        return this.loggerInitialized;
    }

    /**
     * Read Messages from Apache Kafka Brokers for a particular topic. We are using
     * Non-Blocking Consumer since we need to read only specific number of messages for a
     * topic.
     * messageListSize in DataConsumer.properties controls the number of messages to be
     * fetched in one iteration.
     * 
     * @param topic
     * @param consumer
     * @return Group of messages consumed.
     */
    private String consumeAsStream(final String topic, final ConsumerConnector consumer) {
        final Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        final Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumer.createMessageStreams(topicCountMap);
        final KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        final ConsumerIterator<byte[], byte[]> it = stream.iterator();
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        try {
            while (it.hasNext() && count < this.messageListSize) {
                final String message = new String(it.next().message());
                this.logger.info(message);
                sb.append(message + "\n");
                count++;
            }
        } catch (final ConsumerTimeoutException cte) {
            // Shutdown after reading specific messages
            consumer.shutdown();
            this.logger.info(cte.getMessage());
            return sb.toString();
        }

        consumer.shutdown(); // Shutdown after reading specific messages

        return sb.toString();
    }
}
