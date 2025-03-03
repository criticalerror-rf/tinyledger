package com.rehan.tinyledger.adapter.web;

import com.rehan.tinyledger.core.domain.exception.InsufficientFundsException;
import com.rehan.tinyledger.core.domain.exception.TransactionFailedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return new ResponseEntity<>(new ErrorResponse( ErrorMessages.INSUFFICIENT_FUNDS.getMessage(),
                HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInputParseError(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse( ErrorMessages.INVALID_INPUT.getMessage(),
                HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<ErrorResponse> handleFailedTransaction(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse( ErrorMessages.NOT_FOUND.getMessage(),
                HttpStatus.NOT_FOUND.value()),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse( ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add("'"+fieldName + "' "+errorMessage);
        });

        return new ResponseEntity<>(new ErrorResponse(errors.toString(), HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }
}