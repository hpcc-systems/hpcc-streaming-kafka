package org.hpccsystems.streamapi.service;


import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.hpccsystems.streamapi.service.dao.KafkaMessageDao;
import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages="org.hpccsystems.streamapi")
@EnableAutoConfiguration
public class Application {

    private static final int TEN_SECONDS = 10 * 1000;

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.setHeadless(true);
        app.run();
    }

    
    @Autowired
    private ProducerProps producerProps;
    
    @Autowired
    private ConsumerProps consumerProps;
    
    @Bean
    public MessageDao messageDao() {
        return new KafkaMessageDao();
    }
    
    @Bean
    public Producer<?, ?> producer() {
      final ProducerConfig config = new ProducerConfig(this.producerProps.asProperties());
      return new Producer<String, String>(config);
    }
    
    @Bean
    public ConsumerConnector consumerConnector() {
        final ConsumerConfig config = 
                new ConsumerConfig(this.consumerProps.asProperties(TEN_SECONDS));
        return Consumer.createJavaConsumerConnector(config);
    }
}
