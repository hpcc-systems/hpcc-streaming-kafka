package org.hpccsystems.streamapi.service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.hpccsystems.streamapi.common.HpccStreamingException;
import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
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

    private static final String LINK_REL_RESOURCE = "resource";

    @Autowired
    private MessageDao messageDao;

    @RequestMapping(method=POST)
    public @ResponseBody HttpEntity<? extends ResourceSupport> produce(
            @RequestParam(value="topic", required=true) final String topic,
            @RequestParam(value="key", required=true) final List<String> keys,
            @RequestParam(value="message", required=true) final List<String> messages) {

        if (keys.size() != messages.size()) {
            return handleProduceError(topic, keys, messages);
        }
        
        this.messageDao.send(topic, keys, messages);

        final HpccProducerResponse response = newDecoratedProducerResponseFor(topic, keys, messages);

        return new ResponseEntity<HpccProducerResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(method=GET, value="/{topic}")
    public @ResponseBody HttpEntity<? extends ResourceSupport> consume(
            @PathVariable(value="topic") final String topic) {

        try {
            final List<Message> messages = this.messageDao.receive(topic);

            final HpccConsumerResponse response = 
                newDecoratedConsumerResponseFor(topic, messages);

            return new ResponseEntity<HpccConsumerResponse>(response, OK);

        } catch (JsonProcessingException e) {
            final HpccStreamingException wrapEx = new HpccStreamingException(e);
            
            return handleConsumeError(topic, wrapEx);
            
        } catch (HpccStreamingException e) {
            
            return handleConsumeError(topic, e);
        }
    }

    private HttpEntity<? extends ResourceSupport> handleProduceError(final String topic,
            final List<String> keys, final List<String> messages) {
        final ErrorResponse errorResponse =
                new ErrorResponse("Key count and message count must be equal");

        addSelfRelProduceLink(topic, keys, messages, errorResponse);

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private HttpEntity<? extends ResourceSupport> handleConsumeError(final String topic,
            final HpccStreamingException e) {
        final ErrorResponse response = new ErrorResponse(e.getMessage());

        addResourceRelLink(topic, response);
        
        return new ResponseEntity<ErrorResponse>(response, INTERNAL_SERVER_ERROR);
    }

    private HpccProducerResponse newDecoratedProducerResponseFor(final String topic,
            final List<String> keys, final List<String> messages) {

        final HpccProducerResponse response = new HpccProducerResponse();

        addSelfRelProduceLink(topic, keys, messages, response);

        addResourceRelLink(topic, response);

        return response;
    }

    private HpccConsumerResponse newDecoratedConsumerResponseFor(final String topic,
            final List<Message> messages) throws JsonProcessingException {

        final String jsonMessages = toJsonString(messages);

        return new HpccConsumerResponse(jsonMessages);
    }

    private void addSelfRelProduceLink(final String topic, final List<String> keys,
            final List<String> messages, final ResourceSupport response) {
        
        response.add(linkTo(
                methodOn(HpccStreamingController.class).produce(topic, keys, messages))
                .withSelfRel());
    }

    private void addResourceRelLink(final String topic, final ResourceSupport response) {
        response.add(linkTo(
                methodOn(HpccStreamingController.class).consume(topic))
                .withRel(LINK_REL_RESOURCE));
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
