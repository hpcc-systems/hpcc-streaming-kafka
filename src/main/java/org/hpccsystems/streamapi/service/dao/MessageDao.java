package org.hpccsystems.streamapi.service.dao;

import java.util.List;

public interface MessageDao {

    void send(String topic, List<String> keys, List<String> messages);
}
