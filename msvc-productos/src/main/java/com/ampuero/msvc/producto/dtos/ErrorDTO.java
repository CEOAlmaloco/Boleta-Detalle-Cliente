package com.ampuero.msvc.producto.dtos;

/**
 * ErrorDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para representar errores en las respuestas
 * de la API del microservicio de productos. Proporciona información detallada sobre errores
 * que ocurren durante el procesamiento de solicitudes.
 *
 * Uso común:
 * - Respuestas de error HTTP (4xx, 5xx).
 * - Manejo centralizado de excepciones en GlobalExceptionHandler.
 * - Comunicación de errores de validación a clientes de la API.
 * - Logging y monitoreo de errores del sistema.
 *
 * Atributos:
 * - status: Código de estado HTTP del error.
 * - message: Mensaje principal del error.
 * - details: Mensaje detallado con información adicional.
 * - timestamp: Fecha y hora cuando ocurrió el error.
 * - path: Ruta del endpoint donde ocurrió el error.
 * - errors: Mapa de errores específicos (útil para errores de validación).
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [25-06-25]
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar errores de la API")
public class ErrorDTO {

    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;

    @Schema(description = "Mensaje principal del error", example = "Producto no encontrado")
    private String message;

    @Schema(description = "Detalles adicionales del error", example = "El producto con ID 999 no existe en la base de datos")
    private String details;

    @Schema(description = "Fecha y hora cuando ocurrió el error", example = "2024-06-25T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/api/v1/productos/999")
    private String path;

    @Schema(description = "Errores específicos de validación")
    private Map<String, String> errors;

    // Constructor personalizado para errores simples
    public ErrorDTO(int status, String message, String details, String path) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor para errores con timestamp personalizado
    public ErrorDTO(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorDTO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", errors=" + errors +
                '}';
    }
}
