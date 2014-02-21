package org.hpccsystems.streamapi.service;


import java.util.Properties;

import javax.annotation.PreDestroy;

import kafka.consumer.ConsumerConfig;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages="org.hpccsystems.streamapi")
@PropertySource("classpath:application.properties")
public class Application {

    private static final int TEN_SECONDS = 10_000;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Entry point for the Kafka Integration application.
     * 
     * Spring Boot bootstraps, configures, and runs the application
     * in an embedded application container.
     * 
     * @param args
     */
    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.run();
    }

    @Autowired
    private Environment env;
    
    @Bean
    public MessageDao messageDao() {
        return new KafkaMessageDao();
    }

    @Bean
    public Producer<String, String> producer() {
        final ProducerConfig pConfig = new ProducerConfig(producerProps().asProperties());
        return new Producer<String, String>(pConfig);
    }

    @Bean
    public ProducerProps producerProps() {
        return new ProducerProps();
    }
    
    @Bean
    public ConsumerConfig consumerConfig() {
        final ConsumerConfig cConfig = 
                new ConsumerConfig(consumerProps().asProperties(TEN_SECONDS));
        return cConfig;
    }
    
    @Bean
    public ConsumerProps consumerProps() {
        return new ConsumerProps();
    }
    
    @PreDestroy
    public void shutdown() {
        producer().close();
    }

    public class ProducerProps {

        private static final String METADATA_BROKER_LIST  = "metadata.broker.list";
        private static final String SERIALIZER_CLASS      = "serializer.class";
        private static final String PRODUCER_TYPE         = "producer.type";
        private static final String REQUEST_REQUIRED_ACKS = "request.required.acks";

    public Properties asProperties() {
          final Properties p = new Properties();

          p.put(METADATA_BROKER_LIST, env.getProperty(METADATA_BROKER_LIST));
          p.put(SERIALIZER_CLASS, env.getProperty(SERIALIZER_CLASS));
          p.put(PRODUCER_TYPE, env.getProperty(PRODUCER_TYPE));
          p.put(REQUEST_REQUIRED_ACKS, env.getProperty(REQUEST_REQUIRED_ACKS));

          return p;
      }
    }
    
    public class ConsumerProps {

        private static final String ZOOKEEPER_CONNECT            = "zookeeper.connect";
        private static final String CONSUMER_TIMEOUT_MS          = "consumer.timeout.ms";
        private static final String GROUP_ID                     = "group.id";
        private static final String AUTO_OFFSET_RESET            = "auto.offset.reset";

        public Properties asProperties(final Integer timeoutMs) {
            final Properties p = new Properties();

            p.put(ZOOKEEPER_CONNECT, env.getProperty(ZOOKEEPER_CONNECT));
            p.put(CONSUMER_TIMEOUT_MS, String.format("%d", timeoutMs != null
                    ? timeoutMs
                    : env.getProperty(CONSUMER_TIMEOUT_MS)));
            p.put(GROUP_ID, env.getProperty(GROUP_ID));

            p.put(AUTO_OFFSET_RESET, env.getProperty(AUTO_OFFSET_RESET));

            return p;
        }
    }
}
