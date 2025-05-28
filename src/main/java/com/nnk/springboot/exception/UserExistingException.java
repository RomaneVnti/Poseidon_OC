package com.nnk.springboot.exception;

public class UserExistingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserExistingException(String message) {
        super(message);
    }

}