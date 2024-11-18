package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

public class CommonValidationFailedException extends RuntimeException {

    public CommonValidationFailedException(String message) {
        super(message);
    }
}
