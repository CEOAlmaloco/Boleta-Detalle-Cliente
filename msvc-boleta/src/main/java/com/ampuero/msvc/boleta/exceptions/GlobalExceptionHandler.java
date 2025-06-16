package com.ampuero.msvc.boleta.exceptions;

import com.ampuero.msvc.boleta.dtos.ErrorDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorDTO createErrorDTO(int status, LocalDate localDate, Map<String, String> errorMap) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(status);
        errorDTO.setLocalDate(localDate);
        errorDTO.setErrors(errorMap);
        return errorDTO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationFields(MethodArgumentNotValidException exception){
        Map<String,String> errorMap = new HashMap<>();
        LocalDate localDate = LocalDate.now();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.createErrorDTO(HttpStatus.BAD_REQUEST.value(), localDate, errorMap));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> errorMap = Collections.singletonMap("error", ex.getMessage());
        LocalDate localDate = LocalDate.now();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(this.createErrorDTO(HttpStatus.NOT_FOUND.value(), localDate, errorMap));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDTO> handleFeignException(FeignException ex) {
        Map<String, String> errorMap = Collections.singletonMap("error", "Error de comunicación con servicio externo: " + ex.getMessage());
        LocalDate localDate = LocalDate.now();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(this.createErrorDTO(HttpStatus.BAD_GATEWAY.value(), localDate, errorMap));
    }

}