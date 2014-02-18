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
    public void send(final String topic, final List<String> keys, final List<String> messages) {
        final StringBuilder keysBuf = transformWithNewLines(keys);
        final StringBuilder messagesBuf = transformWithNewLines(messages);

        this.log.info(String.format("send: topic [%s], keys [%s], messages [%s]", topic,
                keysBuf.toString(), messagesBuf.toString()));
    }

    private StringBuilder transformWithNewLines(final List<String> items) {
        final StringBuilder buf = new StringBuilder();
        for (final String item : items) {
            buf.append(item).append('\n');
        }
        return buf;
    }

    @Override
    public List<Message> receive(final String topic) throws HpccStreamingException {
        final List<String> data =
                ImmutableList.<String> builder().add("msg1").add("msg2").build();

        List<Message> result = null;

        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);

            final Message element = new Message(new MessageAndMetadata<byte[], byte[]>(
                    "theKey".getBytes(), bos.toByteArray(), "theTopic", 0, 0L));

            result = ImmutableList.<Message> builder().add(element).build();

            this.log.info(String.format("receive: result [%s]", result));

        } catch (final IOException e) {
            throw new HpccStreamingException(e);
        } catch (final HpccStreamingException e) {
            throw e;
        }

        return result;
    }

}
