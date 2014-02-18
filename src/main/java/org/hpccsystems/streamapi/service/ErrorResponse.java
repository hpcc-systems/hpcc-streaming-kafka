package org.hpccsystems.streamapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse extends ResourceSupport {

    @JsonProperty
    final List<String> errors;

    @JsonCreator
    public ErrorResponse(final String... errors) {
        this.errors = new ArrayList<>();
        for (final String err : errors) {
            this.errors.add(err);
        }
    }

    public ErrorResponse add(final String err) {
        this.errors.add(err);
        return this;
    }

    @Override
    public String toString() {
        return "ErrorResponse [errors=" + this.errors + ", getId()=" + getId()
                + ", getLinks()=" + getLinks() + "]";
    }
}
