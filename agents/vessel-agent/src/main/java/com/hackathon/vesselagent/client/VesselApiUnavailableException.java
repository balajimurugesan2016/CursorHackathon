package com.hackathon.vesselagent.client;

public class VesselApiUnavailableException extends RuntimeException {

    public VesselApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
