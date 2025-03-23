package com.alex.web.node.pdm.exception;

public class NameAlreadyExistsException extends RuntimeException{
    public NameAlreadyExistsException(Throwable cause,String message) {
        super(message, cause);
    }
    public NameAlreadyExistsException(String message) {
        super(message);
    }
}
