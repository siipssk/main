package com.flexit.spacetoursinc.common.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(StiBusinessException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Business logic error")
    protected ResponseEntity<String> handleBusinessException(StiBusinessException exception) {
        log.error("Business error: " + exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(StiNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleNotFoundException(StiNotFoundException exception) {
        log.error("Not found error: " + exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.error("Validation error: {}", errors);
        return buildValidationErrorResponse(errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<String> handleGenericException(Exception exception) {
        log.error("Runtime exception: ", exception);
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> buildErrorResponse(Exception exception, HttpStatus httpStatus) {
        return ResponseEntity
                .status(httpStatus)
                .body(exception.getMessage());
    }

    private ResponseEntity<List<String>> buildValidationErrorResponse(List<String> errors) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

}
