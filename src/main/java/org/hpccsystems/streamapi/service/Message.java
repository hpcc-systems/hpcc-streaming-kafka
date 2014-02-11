package org.hpccsystems.streamapi.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.hpccsystems.streamapi.common.HpccStreamingException;

import kafka.message.MessageAndMetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty
    private final String topic;

    @JsonProperty
    private String       key;

    @JsonProperty
    private List<String> data;

    @JsonProperty
    private final int    partition;

    @JsonProperty
    private final long   offset;

    @SuppressWarnings("unchecked")
    public Message(final MessageAndMetadata<byte[], byte[]> message)
        throws HpccStreamingException {
        
        this.topic = message.topic();

        try (ObjectInputStream ois =
                new ObjectInputStream(new ByteArrayInputStream(message.message()));) {

            this.key = new String(message.key(), UTF_8);
            this.data = (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new HpccStreamingException(e);
        }

        this.partition = message.partition();
        this.offset = message.offset();
    }

    public String getTopic() {
        return this.topic;
    }

    public String getKey() {
        return this.key;
    }

    public List<String> getData() {
        return this.data;
    }

    public int getPartition() {
        return this.partition;
    }

    public long getOffset() {
        return this.offset;
    }

    @Override
    public String toString() {
        return "Message [topic=" + this.topic + ", key=" + this.key + ", data="
                + this.data + ", partition=" + this.partition + ", offset=" + this.offset
                + "]";
    }
}