package com.printer.fileque.tools;

import com.printer.fileque.dtos.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseDto<Object>> handleResponseStatusException(ResponseStatusException ex) {
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .success(false)
                .message(ex.getReason())
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, ex.getStatusCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto<Object>> handleRuntimeException(RuntimeException ex) {
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGeneralException() {
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .success(false)
                .message("Ein unerwarteter Fehler ist aufgetreten!")
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}