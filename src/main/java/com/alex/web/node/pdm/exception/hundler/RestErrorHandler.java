package com.alex.web.node.pdm.exception.hundler;

import com.alex.web.node.pdm.api.rest.RestDetailController;
import com.alex.web.node.pdm.api.rest.RestSpecificationController;
import com.alex.web.node.pdm.api.rest.RestUserController;
import com.alex.web.node.pdm.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class contains the methods for handling exceptions which throws in rest-controller layer.
 *
 * @see RestUserController restUserController
 * @see RestSpecificationController restSpecificationController.
 * @see RestDetailController restDetailController.
 */

@RestControllerAdvice(basePackages = "com.alex.web.node.pdm.api.rest")
@Slf4j
public class RestErrorHandler /*extends ResponseEntityExceptionHandler */{

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.info(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.info(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    //UsernameNptFoundException
    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<?> handleEntityCreationException(EntityCreationException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(CodeAlreadyExistsException.class)
    public ResponseEntity<?> handleCodeAlreadyExistsException(CodeAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(NameAlreadyExistsException.class)
    public ResponseEntity<?> handleCodeAlreadyExistsException(NameAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.warn( ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex){
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

   /* @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }*/


}
