package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

public class UserAlreadyExistException extends RuntimeException {

    private String message;

    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}
