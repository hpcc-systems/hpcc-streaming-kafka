package com.hpccsystems.kafka2thor.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpccsystems.kafka2thor.consumer.Consumer;


public class Kafka2ThorConsumerRunner {
	  final static Logger logger = LoggerFactory.getLogger(Kafka2ThorConsumerRunner.class);
	    
	  
	    public static void main(String[] args) {
	        logger.info("Initializing Spring context.");
	        logger.info("Spring context initialized.");	        
	        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
	        Consumer consumer = applicationContext.getBean("consumer", Consumer.class );
	        logger.info("Starting Kafka consumer");	        
	        consumer.start();

	    }
}
