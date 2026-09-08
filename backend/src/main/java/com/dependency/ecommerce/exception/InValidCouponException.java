package com.dependency.ecommerce.exception;

public class InValidCouponException extends RuntimeException{
    public InValidCouponException(String message){
        super(message);
    }
}
