package org.hpccsystems.streamapi.common;


public class HpccStreamingException extends Exception {

    private static final long serialVersionUID = 1L;

    public HpccStreamingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HpccStreamingException(final String message) {
        super(message);
    }

    public HpccStreamingException(final Throwable cause) {
        super(cause);
    }

}
