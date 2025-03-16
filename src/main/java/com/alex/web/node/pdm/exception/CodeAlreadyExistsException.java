package com.alex.web.node.pdm.exception;

public class CodeAlreadyExistsException extends RuntimeException{
    public CodeAlreadyExistsException(String message) {
        super(message);
    }
    public CodeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
