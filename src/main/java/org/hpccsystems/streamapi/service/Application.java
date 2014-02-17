package org.hpccsystems.streamapi.service;


import java.util.Properties;

import kafka.api.OffsetRequest;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.hpccsystems.streamapi.service.dao.KafkaMessageDao;
import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages="org.hpccsystems.streamapi")
//@PropertySource("classpath:/application.properties")
public class Application {

    private static final int TEN_SECONDS = 10 * 1000;

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.run();
    }

    @Bean
    public MessageDao messageDao() {
        return new KafkaMessageDao();
    }

    @Bean
    public Producer<String, String> producer() {
        final ProducerConfig pConfig = new ProducerConfig(new ProducerProps().asProperties());
        return new Producer<String, String>(pConfig);
    }

    @Bean
    public ConsumerConnector consumerConnector() {
        final ConsumerConfig cConfig = 
                new ConsumerConfig(new ConsumerProps().asProperties(TEN_SECONDS));
        return Consumer.createJavaConsumerConnector(cConfig);
    }
    
    public static class ConsumerProps {

//        @Value("${zookeeper.connect:192.168.22.20:2181}")
//        private String zookeeperConnect;
//
//        @Value("${zookeeper.session.timeout.ms:30000}")
//        private String zookeeperSessionTimeoutMs;
//
//        @Value("${zookeeper.sync.time.ms:200}")
//        private String zookeeperSyncTimeMs;
//
//        @Value("${auto.commit.interval.ms:1000}")
//        private String autoCommitIntervalMs;
//
//        @Value("${consumer.timeout.ms:500}")
//        private String consumerTimeoutMs;
//
//        @Value("${messageListSize:500}")
//        private String messageListSize;
//
//        @Value("${group.id:hpcc}")
//        private String groupId;

        public Properties asProperties(final Integer timeoutMs) {
            final Properties p = new Properties();

            p.put("zookeeper.connect", "192.168.22.20:2181");
            p.put("zookeeper.session.timeout.ms", "30000");
            p.put("zookeeper.sync.time.ms", "200");
            p.put("auto.commit.interval.ms", "1000");
            p.put("consumer.timeout.ms", String.format("%d", timeoutMs != null
                    ? timeoutMs
                    : "500"));
            p.put("messageListSize", "500");
            p.put("group.id", "hpcc");

            p.put("auto.offset.reset", OffsetRequest.SmallestTimeString());

            return p;
        }

//        @Override
//        public String toString() {
//            return "ConsumerProps [zookeeperConnect=" + this.zookeeperConnect
//                    + ", zookeeperSessionTimeoutMs=" + this.zookeeperSessionTimeoutMs
//                    + ", zookeeperSyncTimeMs=" + this.zookeeperSyncTimeMs
//                    + ", autoCommitIntervalMs=" + this.autoCommitIntervalMs
//                    + ", consumerTimeoutMs=" + this.consumerTimeoutMs + ", messageListSize="
//                    + this.messageListSize + ", groupId=" + this.groupId + "]";
//        }
    }

    public static class ProducerProps {

//      @Value("${metadata.broker.list:192.168.22.20:9092}")
//      private String metadataBrokerList;
//
//      @Value("${serializer.class:kafka.serializer.StringEncoder}")
//      private String serializerClass;
//
//      @Value("${producer.type:async}")
//      private String producerType;
//
//      @Value("${request.required.acks:1}")
//      private String requestRequiredAcks;

      public Properties asProperties() {
          final Properties p = new Properties();

          p.put("metadata.broker.list", "192.168.22.20:9092");
          p.put("serializer.class", "kafka.serializer.StringEncoder");
          p.put("producer.type", "async");
          p.put("request.required.acks", "1");

          return p;
      }

//        @Override
//        public String toString() {
//            return "ProducerProps [metadataBrokerList=" + this.metadataBrokerList
//                    + ", serializerClass=" + this.serializerClass + ", producerType="
//                    + this.producerType + ", requestRequiredAcks=" + this.requestRequiredAcks + "]";
//        }
    }
}
