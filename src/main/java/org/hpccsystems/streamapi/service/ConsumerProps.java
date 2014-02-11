package org.hpccsystems.streamapi.service;

import java.util.Properties;

import kafka.api.OffsetRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsumerProps {

    @Value("${zookeeper.connect:192.168.22.20:2181}")
    private String zookeeperConnect;

    @Value("${zookeeper.session.timeout.ms}")
    private String zookeeperSessionTimeoutMs;

    @Value("${zookeeper.sync.time.ms}")
    private String zookeeperSyncTimeMs;

    @Value("${auto.commit.interval.ms}")
    private String autoCommitIntervalMs;

    @Value("${consumer.timeout.ms}")
    private String consumerTimeoutMs;

    @Value("${messageListSize}")
    private String messageListSize;

    @Value("${group.id}")
    private String groupId;

    public Properties asProperties(final Integer timeoutMs) {
        final Properties p = new Properties();

        p.put("zookeeper.connect", this.zookeeperConnect);
        p.put("zookeeper.session.timeout.ms", this.zookeeperSessionTimeoutMs);
        p.put("zookeeper.sync.time.ms", this.zookeeperSyncTimeMs);
        p.put("auto.commit.interval.ms", this.autoCommitIntervalMs);
        p.put("consumer.timeout.ms", String.format("%d", timeoutMs != null
                ? timeoutMs
                : this.consumerTimeoutMs));
        p.put("messageListSize", this.messageListSize);
        p.put("group.id", this.groupId);

        p.put("auto.offset.reset", OffsetRequest.SmallestTimeString());

        return p;
    }

    @Override
    public String toString() {
        return "ConsumerProps [zookeeperConnect=" + this.zookeeperConnect
                + ", zookeeperSessionTimeoutMs=" + this.zookeeperSessionTimeoutMs
                + ", zookeeperSyncTimeMs=" + this.zookeeperSyncTimeMs
                + ", autoCommitIntervalMs=" + this.autoCommitIntervalMs
                + ", consumerTimeoutMs=" + this.consumerTimeoutMs + ", messageListSize="
                + this.messageListSize + ", groupId=" + this.groupId + "]";
    }
}
