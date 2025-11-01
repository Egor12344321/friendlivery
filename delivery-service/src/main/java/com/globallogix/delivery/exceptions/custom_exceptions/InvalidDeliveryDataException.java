package com.globallogix.delivery.exceptions.custom_exceptions;

public class InvalidDeliveryDataException extends RuntimeException{
    public InvalidDeliveryDataException(String message) {
        super(message);
    }
}
