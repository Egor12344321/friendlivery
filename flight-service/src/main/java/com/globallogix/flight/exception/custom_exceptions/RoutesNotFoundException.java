package com.globallogix.flight.exception.custom_exceptions;

public class RoutesNotFoundException extends RuntimeException {
    public RoutesNotFoundException(String message) {
        super(message);
    }
}
