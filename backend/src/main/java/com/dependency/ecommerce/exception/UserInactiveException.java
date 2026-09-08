package com.dependency.ecommerce.exception;

public class UserInactiveException extends RuntimeException{
    public UserInactiveException(String message){
        super(message);
    }
}
