package org.hpccsystems.streamapi.service.dao;

import java.util.List;

import org.apache.log4j.Logger;


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
}
