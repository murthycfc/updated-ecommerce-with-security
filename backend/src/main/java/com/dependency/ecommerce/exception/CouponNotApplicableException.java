package com.dependency.ecommerce.exception;

public class CouponNotApplicableException extends RuntimeException {
    public CouponNotApplicableException(String message){
        super(message);
    }
}
