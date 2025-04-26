package com.alex.web.node.pdm.exception;

/**
 * This exception describes the situation when name of detail is already exists.
 */

public class NameAlreadyExistsException extends RuntimeException{
    public NameAlreadyExistsException(Throwable cause,String message) {
        super(message, cause);
    }
    public NameAlreadyExistsException(String message) {
        super(message);
    }
}
