package com.arthur.controle_estoque_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<ApiError> resourceNotFound(ResourceNotFoundException exception){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,exception.getMessage());
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<ApiError> resourceNotFound(BadRequestException exception){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,exception.getMessage());
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ConflictException.class)
    private ResponseEntity<ApiError> resourceNotFound(ConflictException exception){
        ApiError apiError = new ApiError(HttpStatus.CONFLICT,exception.getMessage());
        //return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

}
