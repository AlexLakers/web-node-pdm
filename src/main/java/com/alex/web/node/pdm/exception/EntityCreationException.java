package com.alex.web.node.pdm.exception;

/**
 * This exception describes the situation when some error has been detected during creation process.
 */

public class EntityCreationException extends RuntimeException{
    public EntityCreationException(String message){
        super(message);
    }
    public EntityCreationException(String message, Throwable cause){
        super(message, cause);
    }
}
