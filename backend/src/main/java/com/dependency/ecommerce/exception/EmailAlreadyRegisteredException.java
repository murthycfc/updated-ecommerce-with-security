package com.dependency.ecommerce.exception;

public class EmailAlreadyRegisteredException extends RuntimeException{
    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}
