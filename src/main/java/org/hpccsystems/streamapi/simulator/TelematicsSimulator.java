package org.hpccsystems.streamapi.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hpccsystems.streamapi.consumer.DataConsumer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class TelematicsSimulator implements Runnable {
	
	private static final Logger logger;
	private static String loggerInitialized = "Logger Not Initialized";
	private VehicleSimulator vehicle;  
	private final Producer<String, String> producer;
	private String topic;
	private static String initMessage = "Not initialized";
	private static String brokerList;
	private static String serializerClass;
	private static String acknowledgmentRequired;
	
	static {
		
		logger = Logger.getLogger(TelematicsSimulator.class);
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
				brokerList = props.getProperty("metadata.broker.list");
				serializerClass = props.getProperty("serializer.class");
				acknowledgmentRequired = props.getProperty("request.required.acks");
				initMessage = "Completed Initialization";
			} catch (IOException e) {
				initMessage = e.getMessage();
				logger.error(e.getMessage());
			}
		}
	}
	
	public TelematicsSimulator(String topic, String plateCode) {
		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", serializerClass);
		props.put("request.required.acks", acknowledgmentRequired);
		
		producer = new Producer<String, String>(new ProducerConfig(props));
		this.topic = topic;
		this.vehicle = new VehicleSimulator(plateCode);
	}
	
	/**
	 * Simulate some test data
	 */
	private void simulate() {
		
		accelerateToSteadyState();
		
		publishMessages(); 
		
		// Reduce the speed by 5 miles/sec until halt
		vehicle.stop(); //Gradually come to a halt. Simulates a stop at traffic light or stop sign
		
		publishMessages(); 
		
		vehicle.passive(120); //Wait for a couple of mins
		
		publishMessages(); 
		
		accelerateToSteadyState();
		
		publishMessages();
		
		vehicle.cruiseAtSameSpeed(200);
		
		publishMessages();
		
		slowdown();
		
		publishMessages();
		
	}
	
	private void accelerateToSteadyState() {
		for (int i = 0; i < 4; i++) {
			vehicle.acceleratePerSecond(5);
		}
		
		for (int i = 0; i < 2; i++) {
			vehicle.acceleratePerSecond(6);
		}
		
		for (int i = 0; i < 2; i++) {
			vehicle.acceleratePerSecond(9);
		}
		
		for (int i = 0; i < 2; i++) {
			vehicle.acceleratePerSecond(12);
		}
				
		for (int i = 0; i < 40; i++) {
			vehicle.acceleratePerSecond(1);
			vehicle.deacceleratePerSecond(1);
		}
	}
	
	private void slowdown() {
		for (int i = 0; i < 4; i++) {
			vehicle.deacceleratePerSecond(2);
		}
		for (int i = 0; i < 4; i++) {
			vehicle.deacceleratePerSecond(6);
		}
		for (int i = 0; i < 2; i++) {
			vehicle.deacceleratePerSecond(8);
		}
		for (int i = 0; i < 2; i++) {
			vehicle.deacceleratePerSecond(11);
		}
	}
	
	/**
	 * Publish the Messages to Kafka Brokers
	 */
	public void publishMessages() {
		ArrayList<String> messageList = vehicle.getData();
		for (int i = 0; i < messageList.size(); i++) {
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, messageList.get(i));
			producer.send(data);
		}
		
		vehicle.clearData();
	}
	
	public static String initMessage() {
		return initMessage;
	}
	
	public static String isLoggerInitialized() {
		return loggerInitialized;
	}
	
	@Override
	public void run() {
		simulate();
	}
	
	public static void simulate(String[] plateCodes) {
		Thread thread = new Thread(new TelematicsSimulator("vehicle-simulator", "F11111"));
		thread.run();
	}
    
	public static void main(String[] codes) {
		simulate(null);
	}

}
