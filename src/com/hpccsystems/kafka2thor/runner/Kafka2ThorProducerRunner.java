package com.hpccsystems.kafka2thor.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpccsystems.kafka2thor.producer.Producer;

public class Kafka2ThorProducerRunner {
	
	final static Logger logger = LoggerFactory.getLogger(Kafka2ThorProducerRunner.class);
    
	  
    public static void main(String[] args) {
        logger.info("Initializing Spring context.");
        logger.info("Spring context initialized.");	        
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
        Producer producer = applicationContext.getBean("producer", Producer.class );
        logger.info("Starting Kafka Producer");	        
        producer.start();

    }
}
