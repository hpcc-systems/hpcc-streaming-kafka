package org.hpccsystems.streamapi.service;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ProducerProps {

  @Value("${metadata.broker.list:192.168.22.30:9092}")
  private String metadataBrokerList;

  @Value("${serializer.class}")
  private String serializerClass;

  @Value("${producer.type}")
  private String producerType;

  @Value("${request.required.acks}")
  private String requestRequiredAcks;

  public Properties asProperties() {
      final Properties p = new Properties();

      p.put("metadata.broker.list", this.metadataBrokerList);
      p.put("serializer.class", this.serializerClass);
      p.put("producer.type", this.producerType);
      p.put("request.required.acks", this.requestRequiredAcks);

      return p;
  }

    @Override
    public String toString() {
        return "ProducerProps [metadataBrokerList=" + this.metadataBrokerList
                + ", serializerClass=" + this.serializerClass + ", producerType="
                + this.producerType + ", requestRequiredAcks=" + this.requestRequiredAcks + "]";
    }
}
