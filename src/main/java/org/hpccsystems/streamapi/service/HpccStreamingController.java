package org.hpccsystems.streamapi.service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/hpccstream")
public class HpccStreamingController {

    @Autowired
    private MessageDao messageDao;

    @RequestMapping(method=POST)
    public HttpEntity<? extends ResourceSupport> produce(
            @RequestParam(value="topic", required=true) final String topic,
            @RequestParam(value="key", required=true) final List<String> keys,
            @RequestParam(value="message", required=true) final List<String> messages) {

        if (keys.size() != messages.size()) {
            return handleProduceError(topic, keys, messages);
        }
        
        this.messageDao.send(topic, keys, messages);

        final HpccProducerResponse response = newDecoratedProducerResponse();

        return new ResponseEntity<HpccProducerResponse>(response, HttpStatus.OK);
    }

    private HpccProducerResponse newDecoratedProducerResponse() {

        final HpccProducerResponse response = new HpccProducerResponse();

        addSelfRelProduceLink(response);

        return response;
    }

    private <T extends ResourceSupport> void addSelfRelProduceLink(final T response) {
        
        response.add(linkTo(HpccStreamingController.class).withSelfRel());
    }

    private HttpEntity<ErrorResponse> handleProduceError(final String topic,
            final List<String> keys, final List<String> messages) {
        final ErrorResponse errorResponse =
                new ErrorResponse("Key count and message count must be equal");

        addSelfRelProduceLink(errorResponse);

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
