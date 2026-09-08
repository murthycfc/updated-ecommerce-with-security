package com.dependency.ecommerce.exception;

public class CartItemNotFoundException extends RuntimeException{
    public CartItemNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
