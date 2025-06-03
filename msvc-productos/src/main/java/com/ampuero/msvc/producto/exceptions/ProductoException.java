package com.ampuero.msvc.producto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ProductoException extends RuntimeException {
    public ProductoException(String message) {
        super(message);
    }
    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<String> handleProductoException(ProductoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
