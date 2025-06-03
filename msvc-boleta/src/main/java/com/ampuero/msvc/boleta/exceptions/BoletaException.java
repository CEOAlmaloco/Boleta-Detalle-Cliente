package com.ampuero.msvc.boleta.exceptions;

public class BoletaException extends RuntimeException {
    public BoletaException(String message) {
        super(message);
    }

    public BoletaException(String message, Throwable cause) {
        super(message, cause);
    }
}
