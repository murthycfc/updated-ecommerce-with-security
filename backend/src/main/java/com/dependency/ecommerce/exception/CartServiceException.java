package com.dependency.ecommerce.exception;

public class CartServiceException extends RuntimeException{
    public CartServiceException(String message){
        super(message);
    }
}
