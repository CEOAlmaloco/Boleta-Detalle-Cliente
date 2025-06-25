package com.ampuero.msvc.producto.exceptions;

/**
 * GlobalExceptionHandler.java
 *
 * Descripción:
 * Manejador global de excepciones para el microservicio de productos. Centraliza
 * el manejo de todas las excepciones que pueden ocurrir en la aplicación y
 * proporciona respuestas HTTP estructuradas y consistentes.
 *
 * Funcionalidades:
 * - Manejo centralizado de excepciones específicas del dominio.
 * - Transformación de excepciones en respuestas HTTP apropiadas.
 * - Formateo consistente de mensajes de error.
 * - Logging automático de errores para monitoreo.
 * - Manejo de errores de validación con detalles específicos.
 *
 * Excepciones manejadas:
 * - ProductoException: Errores específicos del dominio de productos.
 * - ResourceNotFoundException: Recursos no encontrados.
 * - MethodArgumentNotValidException: Errores de validación de datos.
 * - FeignException: Errores de comunicación con microservicios.
 * - Exception: Errores no específicos (fallback).
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [25-06-25]
 */

import com.ampuero.msvc.producto.dtos.ErrorDTO;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores de validación de campos en DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationFields(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setMessage("Errores de validación en los datos enviados");
        errorDTO.setDetails("Se encontraron " + errorMap.size() + " errores de validación");
        errorDTO.setTimestamp(LocalDateTime.now());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setErrors(errorMap);

        logger.warn("Errores de validación en {}: {}", request.getRequestURI(), errorMap);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Maneja excepciones cuando un recurso no es encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        logger.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    /**
     * Maneja errores de comunicación con microservicios externos.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDTO> handleFeignException(FeignException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        String message = "Error de comunicación con servicio externo";
        String details = "No se pudo establecer comunicación: " + ex.getMessage();

        // Ajustar estado según el tipo de error Feign
        if (ex.status() == 404) {
            status = HttpStatus.NOT_FOUND;
            message = "Servicio externo no encontrado";
        } else if (ex.status() >= 400 && ex.status() < 500) {
            status = HttpStatus.valueOf(ex.status());
            message = "Error en solicitud a servicio externo";
        }

        ErrorDTO errorDTO = new ErrorDTO(
                status.value(),
                message,
                details,
                request.getRequestURI()
        );

        logger.error("Error Feign en {}: Status {} - {}", request.getRequestURI(), ex.status(), ex.getMessage());

        return ResponseEntity.status(status).body(errorDTO);
    }

    /**
     * Maneja excepciones específicas del dominio de productos.
     */
    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<ErrorDTO> handleProductoException(ProductoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String mensaje = ex.getMessage().toLowerCase();

        // Determinar el estado HTTP basado en el mensaje
        if (mensaje.contains("no encontrado") || mensaje.contains("no existe")) {
            status = HttpStatus.NOT_FOUND;
        } else if (mensaje.contains("ya existe") || mensaje.contains("duplicado")) {
            status = HttpStatus.CONFLICT;
        } else if (mensaje.contains("no autorizado") || mensaje.contains("acceso denegado")) {
            status = HttpStatus.FORBIDDEN;
        }

        ErrorDTO errorDTO = new ErrorDTO(
                status.value(),
                "Error en operación de producto",
                ex.getMessage(),
                request.getRequestURI()
        );

        logger.error("ProductoException en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(status).body(errorDTO);
    }

    /**
     * Maneja excepciones no específicas como fallback.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ha ocurrido un error inesperado. Por favor contacte al administrador.",
                request.getRequestURI()
        );

        logger.error("Error no manejado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    /**
     * Maneja errores de argumentos ilegales.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Argumento inválido",
                ex.getMessage(),
                request.getRequestURI()
        );

        logger.warn("Argumento ilegal en {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}