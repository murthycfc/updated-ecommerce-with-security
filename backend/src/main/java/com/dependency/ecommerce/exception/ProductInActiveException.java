package com.dependency.ecommerce.exception;

public class ProductInActiveException extends RuntimeException{
    public ProductInActiveException(String message){
        super(message);
    }
}
