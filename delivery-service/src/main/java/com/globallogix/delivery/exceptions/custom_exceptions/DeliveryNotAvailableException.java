package com.globallogix.delivery.exceptions.custom_exceptions;

public class DeliveryNotAvailableException extends RuntimeException{
    public DeliveryNotAvailableException(String message) {
        super(message);
    }
}
