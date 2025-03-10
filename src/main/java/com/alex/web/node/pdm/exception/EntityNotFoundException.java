package com.alex.web.node.pdm.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(Throwable cause, String message) {
        super(message,cause);
    }
}
