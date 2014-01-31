package com.hpccsystems.kafka2thor.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.hpccsystems.etl.event.LoadableDataReceivedEvent;
import com.hpccsystems.kafka2thor.message.MessageConverter;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

public class Consumer extends Thread implements InitializingBean, ApplicationEventPublisherAware {
	private ConsumerConnector consumer;
	private final String topic;
	private Properties properties;
	private ApplicationEventPublisher applicationEventPublisher;
	private MessageConverter messageConverter;
	
	public Consumer(String topic, Properties properties) {
		this.topic = topic;
		this.properties = properties;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}


	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<Message>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<Message> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<Message> it = stream.iterator();
		while (it.hasNext()) {
			MessageAndMetadata<Message> messageAndMetadata = it.next();
			Message message = messageAndMetadata.message();
			publishDataReceivedEventForEtl(message);
		}
	}

	private void publishDataReceivedEventForEtl(Message message) {
		byte[] data = messageConverter.convertToBytes(message);
		LoadableDataReceivedEvent dataRecievedEvent = new LoadableDataReceivedEvent(this);
		dataRecievedEvent.setData(data);
		applicationEventPublisher.publishEvent(dataRecievedEvent);
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConsumerConfig consumerConfig = new ConsumerConfig(properties);
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
		
	}
}
