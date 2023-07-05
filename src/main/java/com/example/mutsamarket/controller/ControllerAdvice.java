package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(ResponseStatusException e) {
        ResponseDto response = new ResponseDto();
        response.setMessage(e.getMessage());
        if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
            response.setMessage(e.getMessage()+ " writer및 password가 틀리거나 잘못된 요청입니다.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);

        } else if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
            response.setMessage(e.getMessage()+ " 대상을 찾을 수 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
        //처리 할 수 없는 경우 INTERNAL_SERVER_ERROR로 에러 반환
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException exception
    ){
        Map<String, String> errors = new HashMap<>();
        for (FieldError error: exception.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
