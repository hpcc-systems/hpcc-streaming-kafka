package org.hpccsystems.streamapi.service.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;

import org.hpccsystems.streamapi.common.HpccStreamingException;
import org.hpccsystems.streamapi.service.Message;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaMessageDao implements MessageDao {

    @Autowired
    private Producer<String, String> producer;

    @Autowired
    private ConsumerConfig           consumerConfig;
    
    @Override
    public void send(final String topic, final List<String> keys, final List<String> messages) {
        final List<KeyedMessage<String, String>> keyedMessages =
                newKeyedMessagesFrom(topic, keys, messages);

        this.producer.send(keyedMessages);
    }

    private List<KeyedMessage<String, String>> newKeyedMessagesFrom(
            final String topic, final List<String> keys, final List<String> messages) {

        final List<KeyedMessage<String, String>> keyedMessages =
                new ArrayList<>(keys.size());

        for (int i = 0; i < keys.size(); i++) {
            final KeyedMessage<String, String> keyedMessage =
                    new KeyedMessage<>(topic, keys.get(i), messages.get(i));
            keyedMessages.add(keyedMessage);
        }

        return keyedMessages;
    }

    @Override
    public List<Message> receive(final String topic) throws HpccStreamingException {
        final ConsumerConnector consumerConnector = 
                Consumer.createJavaConsumerConnector(consumerConfig);
        
        final KafkaStream<byte[], byte[]> stream =
                newKafkaStreamFor(consumerConnector, topic);
        
        final List<Message> messages = new ArrayList<>();
        try {
            for (final MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
                messages.add(new Message(messageAndMetadata));
            }
        } catch (final ConsumerTimeoutException e) {
            throw new HpccStreamingException(e);
        } finally {
            consumerConnector.commitOffsets();
            consumerConnector.shutdown();
        }

        return messages;
    }

    private KafkaStream<byte[], byte[]> newKafkaStreamFor(
            final ConsumerConnector consumerConnector, final String topic) {

        final Map<String, Integer> streamCounts = Collections.singletonMap(topic, 1);

        final Map<String, List<KafkaStream<byte[], byte[]>>> streams =
                consumerConnector.createMessageStreams(streamCounts);

        return streams.get(topic).get(0);
    }

}
