package com.globallogix.auth.exception;

public class InvalidTokenRefreshException extends RuntimeException{
    public InvalidTokenRefreshException(String message){super(message);}
}
