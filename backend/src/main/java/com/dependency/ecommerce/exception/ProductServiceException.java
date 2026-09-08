package com.dependency.ecommerce.exception;

public class ProductServiceException extends RuntimeException{
    public ProductServiceException(String message){
        super(message);
    }
}
