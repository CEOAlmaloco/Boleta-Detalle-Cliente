package com.ampuero.msvc.producto.dtos;

/**
 * ProductoResponseDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para las respuestas detalladas de productos.
 * Este DTO incluye toda la información del producto más metadatos adicionales como fechas
 * de creación y modificación. Diseñado para ser usado con HATEOAS.
 *
 * Uso común:
 * - Respuestas GET detalladas de productos.
 * - Respuestas POST/PUT tras crear o actualizar productos.
 * - Integración con sistemas externos que necesitan información completa.
 * - Soporte para navegación HATEOAS.
 *
 * Atributos:
 * - idProducto: Identificador único del producto.
 * - nombreProducto: Nombre descriptivo del producto.
 * - descripcionProducto: Descripción detallada del producto.
 * - precioProducto: Precio actual del producto.
 * - fechaCreacion: Timestamp de cuando fue creado el producto (opcional).
 * - fechaModificacion: Timestamp de la última modificación (opcional).
 * - activo: Indica si el producto está disponible para venta.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [25-06-25]
 * Última modificación: [25-06-25]
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta completa para productos")
public class ProductoResponseDTO extends RepresentationModel<ProductoResponseDTO> {

    @Schema(description = "Identificador único del producto", example = "1")
    private Long idProducto;

    @Schema(description = "Nombre del producto", example = "Laptop Gaming Asus ROG")
    private String nombreProducto;

    @Schema(description = "Descripción detallada del producto", example = "Laptop para gaming con procesador Intel i7, 16GB RAM, tarjeta gráfica RTX 4060")
    private String descripcionProducto;

    @Schema(description = "Precio del producto en pesos chilenos", example = "899990.99")
    private Double precioProducto;

    @Schema(description = "Fecha de creación del producto", example = "2024-06-25T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última modificación", example = "2024-06-25T14:45:00")
    private LocalDateTime fechaModificacion;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo;

    @Schema(description = "Número de unidades disponibles en inventario", example = "25")
    private Integer stock;
} 