package com.ampuero.msvc.producto.exceptions;

/**
 * ProductoException.java
 *
 * Descripción:
 * Excepción personalizada para el dominio de productos. Esta excepción se lanza cuando
 * ocurren errores específicos relacionados con las operaciones de productos que no
 * pueden ser manejadas por excepciones estándar.
 *
 * Casos de uso comunes:
 * - Producto no encontrado durante operaciones de consulta.
 * - Violaciones de reglas de negocio específicas de productos.
 * - Errores de validación complejos no cubiertos por validaciones estándar.
 * - Conflictos de estado en operaciones de productos.
 *
 * Esta excepción es capturada y manejada por el GlobalExceptionHandler para
 * proporcionar respuestas HTTP apropiadas y mensajes de error estructurados.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [25-06-25]
 */
public class ProductoException extends RuntimeException {

    /**
     * Constructor básico que acepta un mensaje de error.
     *
     * @param message Mensaje descriptivo del error ocurrido
     */
    public ProductoException(String message) {
        super(message);
    }

    /**
     * Constructor que acepta un mensaje de error y la causa raíz.
     *
     * @param message Mensaje descriptivo del error ocurrido
     * @param cause Excepción que causó este error
     */
    public ProductoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor que acepta solo la causa raíz.
     *
     * @param cause Excepción que causó este error
     */
    public ProductoException(Throwable cause) {
        super(cause);
    }
}
