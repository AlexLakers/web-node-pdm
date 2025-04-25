package com.alex.web.node.pdm.exception;

/**
 * This exception describes the situation when entity is not found.
 */

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(Throwable cause, String message) {
        super(message,cause);
    }
}
