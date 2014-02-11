package org.hpccsystems.streamapi.service.dao;

import java.util.List;

import org.hpccsystems.streamapi.common.HpccStreamingException;
import org.hpccsystems.streamapi.service.Message;

public interface MessageDao {

    void send(String topic, List<String> data);
    
    List<Message> receive(String topic) throws HpccStreamingException;
}
