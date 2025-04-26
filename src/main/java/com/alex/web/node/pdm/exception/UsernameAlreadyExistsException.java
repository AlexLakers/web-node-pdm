package com.alex.web.node.pdm.exception;

/**
 * This exception describes the situation when username of user is already exists.
 */

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
