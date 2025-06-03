package com.ampuero.msvc.detalle.exceptions;

import com.ampuero.msvc.detalle.dtos.ErrorDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorDTO createErrorDTO(int status, Date date, Map<String, String> errorMap) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(status);
        errorDTO.setDate(date);
        errorDTO.setErrors(errorMap);
        return errorDTO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationFields(MethodArgumentNotValidException exception){
        Map<String,String> errorMap = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.createErrorDTO(HttpStatus.BAD_REQUEST.value(), new Date(), errorMap));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> errorMap = Collections.singletonMap("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(this.createErrorDTO(HttpStatus.NOT_FOUND.value(), new Date(), errorMap));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDTO> handleFeignException(FeignException ex) {
        Map<String, String> errorMap = Collections.singletonMap("error", "Error de comunicación con servicio externo: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(this.createErrorDTO(HttpStatus.BAD_GATEWAY.value(), new Date(), errorMap));
    }

    @ExceptionHandler(DetalleException.class)
    public ResponseEntity<ErrorDTO> handleDetalleException(DetalleException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // Default
        if (ex.getMessage().toLowerCase().contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().toLowerCase().contains("ya existe") || ex.getMessage().toLowerCase().contains("conflicto")) {
            status = HttpStatus.CONFLICT;
        }
        Map<String, String> errorMap = Collections.singletonMap("error", ex.getMessage());
        return ResponseEntity.status(status)
                .body(this.createErrorDTO(status.value(), new Date(), errorMap));
    }
}