package com.atorres.nttdata.prodactivems.controller;

import com.atorres.nttdata.prodactivems.exception.CustomException;
import com.atorres.nttdata.prodactivems.exception.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorDto> customExceptionHandler(CustomException ex){
        ErrorDto error = ErrorDto
                .builder()
                .httpStatus(ex.getStatus())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error,ex.getStatus());
    }

}
