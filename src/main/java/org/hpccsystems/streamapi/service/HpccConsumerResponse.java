package org.hpccsystems.streamapi.service;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HpccConsumerResponse extends ResourceSupport {

    private final String message;

    @JsonCreator
    public HpccConsumerResponse(final @JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "HpccConsumerResponse [message=" + this.message + ", getId()=" + getId()
                + ", hasLinks()=" + hasLinks() + ", getLinks()=" + getLinks() + "]";
    }

}
