package org.hpccsystems.streamapi.service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;

import org.hpccsystems.streamapi.common.HpccStreamingException;
import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/hpcc/stream")
public class HpccStreamingController {

    private static final String LINK_REL_PRODUCER = "producer";

    private static final String LINK_REL_RESOURCE = "resource";

    @Autowired
    private MessageDao messageDao;

    @RequestMapping(method=POST)
    public @ResponseBody HttpEntity<HpccProducerResponse> produce(
            @RequestParam(value="topic", required=true) final String topic,
            @RequestParam(value="data", required=true) final List<String> data) {

        this.messageDao.send(topic, data);

        final HpccProducerResponse response = newDecoratedProducerResponseFor(topic, data);

        return new ResponseEntity<HpccProducerResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(method=GET, value="/{topic}")
    public @ResponseBody HttpEntity<HpccConsumerResponse> consume(
            @PathVariable(value="topic") final String topic) {

        HpccConsumerResponse response = null;
        try {
            final List<Message> messages = this.messageDao.receive(topic);

            response = newDecoratedConsumerResponseFor(topic, messages);

            return new ResponseEntity<HpccConsumerResponse>(response, OK);

        } catch (JsonProcessingException | HpccStreamingException e) {
            return new ResponseEntity<HpccConsumerResponse>(response, INTERNAL_SERVER_ERROR);
        }
    }

    private HpccProducerResponse newDecoratedProducerResponseFor(final String topic,
            final List<String> messages) {

        final HpccProducerResponse response = new HpccProducerResponse();

        response.add(linkTo(
                methodOn(HpccStreamingController.class).produce(topic, messages))
                .withSelfRel());

        response.add(linkTo(
                methodOn(HpccStreamingController.class).consume(topic))
                .withRel(LINK_REL_RESOURCE));

        return response;
    }

    private HpccConsumerResponse newDecoratedConsumerResponseFor(final String topic, final List<Message> messages) throws JsonProcessingException {

        final String jsonMessages = toJsonString(messages);

        final HpccConsumerResponse response = new HpccConsumerResponse(jsonMessages);

        response.add(linkTo(methodOn(HpccStreamingController.class).consume(topic))
                .withSelfRel());

        final List<String> sampleData = new ArrayList<String>();
        sampleData.add("$data");

        response.add(linkTo(
                methodOn(HpccStreamingController.class).produce(topic, sampleData)).withRel(LINK_REL_PRODUCER));

        return response;
    }

    private String toJsonString(final List<Message> messages)
        throws JsonProcessingException {

        final ObjectMapper mapper = new ObjectMapper();

        final String jsonMessages =
                mapper.writerWithType(new TypeReference<List<Message>>() {})
                        .writeValueAsString(messages);

        return jsonMessages;
    }
}
