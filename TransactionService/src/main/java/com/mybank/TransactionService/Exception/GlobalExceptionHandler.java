package com.mybank.TransactionService.Exception;	

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mybank.TransactionService.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDataNotFoundException(DataNotFoundException ex,HttpServletRequest request) {
    	ErrorResponseDto  errorResponse = new ErrorResponseDto(HttpStatus.NOT_FOUND.toString(), HttpStatus.NOT_FOUND.value() ,  ex.getMessage(),ex.getDescription(), new Date(),request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException ex,HttpServletRequest request) {
    	ErrorResponseDto  errorResponse = new ErrorResponseDto(HttpStatus.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST.value() ,  ex.getMessage(),ex.getDescription(), new Date(),request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
