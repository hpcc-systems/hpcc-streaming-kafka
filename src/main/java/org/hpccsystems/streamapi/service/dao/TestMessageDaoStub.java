package org.hpccsystems.streamapi.service.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import kafka.message.MessageAndMetadata;

import org.apache.log4j.Logger;
import org.hpccsystems.streamapi.common.HpccStreamingException;
import org.hpccsystems.streamapi.service.Message;

import com.google.common.collect.ImmutableList;


public class TestMessageDaoStub implements MessageDao {

    private final Logger log = Logger.getLogger("org.hpccsystems.streamapi");
    
    @Override
    public void send(final String topic, final List<String> data) {
        final StringBuilder dataBuf = new StringBuilder();
        for (String item : data) {
            dataBuf.append(item).append('\n');
        }
        
        this.log.info(String.format("send: topic [%s], data [%s]", topic,
                dataBuf.toString()));
    }

    @Override
    public List<Message> receive(final String topic) throws HpccStreamingException {
        final List<String> data =
                ImmutableList.<String> builder().add("data1").add("data2").build();
        
        List<Message> result = null;
        
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            
            final Message element = new Message(new MessageAndMetadata<byte[], byte[]>(
                    "theKey".getBytes(), bos.toByteArray(), "theTopic", 0, 0L));
            
            result = ImmutableList.<Message> builder().add(element).build();
            
            this.log.info(String.format("receive: result [%s]", result));
            
        } catch (IOException e) {
            throw new HpccStreamingException(e);
        } catch (HpccStreamingException e) {
            throw e;
        }
        
        return result;
    }

}
