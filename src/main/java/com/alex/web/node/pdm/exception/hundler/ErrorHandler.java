package com.alex.web.node.pdm.exception.hundler;

import com.alex.web.node.pdm.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = "com.alex.web.node.pdm.controller")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(EntityCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUserCreationException(EntityCreationException ex, Model model) {
        log.error(ex.getMessage());
        model.addAttribute("error", new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUsernameNotFoundException(UsernameNotFoundException ex, Model model) {
        log.warn(ex.getMessage());
        model.addAttribute("error", new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(EntityNotFoundException ex, Model model) {
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, Model model){
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler(CodeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleCodeAlreadyExists(CodeAlreadyExistsException ex, Model model){
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler(NameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNameAlreadyExists(NameAlreadyExistsException ex, Model model){
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return "error/error";
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(BindException ex, Model model) {
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        return "error/error";
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model){
        log.warn(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.FORBIDDEN.value(),ex.getMessage()));
        return "error/error";
    }
/*    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model){
        log.error(ex.getMessage());
        model.addAttribute("error",new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        return "error/error";
    }*/


}
