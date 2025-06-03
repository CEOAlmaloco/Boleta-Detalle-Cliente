package com.ampuero.msvc.detalle.exceptions;

public class DetalleException extends RuntimeException {
    public DetalleException(String message) {
        super(message);
    }

    public DetalleException(String message, Throwable cause) {
        super(message, cause);
    }
}
