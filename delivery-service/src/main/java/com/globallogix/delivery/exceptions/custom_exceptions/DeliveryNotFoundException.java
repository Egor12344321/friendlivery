package com.globallogix.delivery.exceptions.custom_exceptions;

public class DeliveryNotFoundException extends RuntimeException{
    public DeliveryNotFoundException(String message){
        super(message);
    }
}
