package com.alex.web.node.pdm.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
