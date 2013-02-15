package com.hpccsystems.kafka2thor.producer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.beans.factory.InitializingBean;
import kafka.javaapi.producer.ProducerData;
import kafka.producer.ProducerConfig;

public class Producer extends Thread implements InitializingBean {
	
	private kafka.javaapi.producer.Producer<Integer, String> producer;
	private final String topic;
	private Properties props;

	public Producer(String topic, Properties properties) {
		this.topic = topic;
		this.props = properties;
	}
	
	public String getMessage() {
		String message = null;
		try {
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    message = bufferRead.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	/** Read the contents of the given file. */
	private ArrayList<String> readFile(String fFileName, String fEncoding) throws IOException {
		ArrayList<String> arlTokens = new ArrayList<String>();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(fFileName), fEncoding);
		try {
			while (scanner.hasNextLine()) {
				arlTokens.add(scanner.nextLine()+ NL);
			}
		} finally {
			scanner.close();
		}

		return arlTokens; 
	}
	
	public void run() {
		String messageStr = null;
		String fileName = "C:\\Dino\\TwitterTweets1352493144311.xml.Pipe"; 
		String encoding = "ISO-8859-1";
		try {
			//To read the messages from a file
			ArrayList<String> arlTokens = readFile(fileName, encoding);
			
			for (Iterator<String> iterator = arlTokens.iterator(); iterator.hasNext();) {
				messageStr = iterator.next();
				producer.send(new ProducerData<Integer, String>(topic, messageStr));
				messageStr = null;
			}
			
			//To read the messages from Console
			//getMessageFromConsole();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	/** Read the contents entered by the user from console. */
	public void getMessageFromConsole(){
		String message = null;
		do { 
			message = getMessage();
			if(message != null) {
				producer.send(new ProducerData<Integer, String>(topic, message));
			}
		} while(message != null);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ProducerConfig config = new ProducerConfig(props);
		producer = new kafka.javaapi.producer.Producer<Integer, String>(config);
	}
	
}
