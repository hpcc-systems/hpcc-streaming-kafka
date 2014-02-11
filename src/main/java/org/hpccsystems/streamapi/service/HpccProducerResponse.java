package org.hpccsystems.streamapi.service;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;

public class HpccProducerResponse extends ResourceSupport {

    @JsonCreator
    public HpccProducerResponse() {
    }

    @Override
    public String toString() {
        return "HpccProducerResponse [getId()=" + getId() + ", hasLinks()=" + hasLinks()
                + ", getLinks()=" + getLinks() + "]";
    }
}
