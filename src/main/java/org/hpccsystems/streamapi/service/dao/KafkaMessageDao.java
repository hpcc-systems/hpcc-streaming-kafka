package org.hpccsystems.streamapi.service.dao;

import java.util.ArrayList;
import java.util.List;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class KafkaMessageDao implements MessageDao {

    @Autowired
    private Producer<String, String> producer;

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
}
