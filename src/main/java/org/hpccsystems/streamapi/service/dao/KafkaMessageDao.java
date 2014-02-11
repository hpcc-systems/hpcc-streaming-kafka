package org.hpccsystems.streamapi.service.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private ConsumerConnector        consumerConnector;

    @Override
    public void send(final String topic, final List<String> data) {
        final List<KeyedMessage<String, String>> keyedMessages =
                newKeyedMessagesFrom(data, topic);

        this.producer.send(keyedMessages);
    }

    @Override
    public List<Message> receive(final String topic) throws HpccStreamingException {
        final KafkaStream<byte[], byte[]> stream = newKafkaStreamFor(topic);

        final List<Message> messages = obtainMessagesFrom(stream);

        return messages;
    }

    private List<KeyedMessage<String, String>> newKeyedMessagesFrom(
            final List<String> data, final String topic) {
        final List<KeyedMessage<String, String>> keyedMessages =
                new ArrayList<KeyedMessage<String, String>>(data.size());

        for (final String datum : data) {
            final KeyedMessage<String, String> keyedMessage =
                    new KeyedMessage<String, String>(topic, datum);
            keyedMessages.add(keyedMessage);
        }
        return keyedMessages;
    }

    private KafkaStream<byte[], byte[]> newKafkaStreamFor(final String topic) {

        final Map<String, Integer> streamCounts = Collections.singletonMap(topic, 1);

        final Map<String, List<KafkaStream<byte[], byte[]>>> streams =
                this.consumerConnector.createMessageStreams(streamCounts);

        final KafkaStream<byte[], byte[]> stream = streams.get(topic).get(0);

        return stream;
    }

    private List<Message> obtainMessagesFrom(final KafkaStream<byte[], byte[]> stream)
        throws HpccStreamingException {

        final List<Message> messages = new ArrayList<Message>();
        try {
            for (final MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
                messages.add(new Message(messageAndMetadata));
            }
        } catch (final ConsumerTimeoutException e) {
            throw new HpccStreamingException(e);
        } finally {
            this.consumerConnector.commitOffsets();
            this.consumerConnector.shutdown();
        }

        return messages;
    }

}
