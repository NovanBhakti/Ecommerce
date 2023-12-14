package com.example.projectv1.handler;

import com.example.projectv1.response.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductIdNotFoundException.class)
    public ResponseEntity<Object> handleProductIdNotFoundException(ProductIdNotFoundException ex) {
        return GlobalResponse.responseHandler(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }
}
