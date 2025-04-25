package com.alex.web.node.pdm.exception;

/**
 * This exception describes the situation when code of specification is already exists.
 */

public class CodeAlreadyExistsException extends RuntimeException{
    public CodeAlreadyExistsException(String message) {
        super(message);
    }
    public CodeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
