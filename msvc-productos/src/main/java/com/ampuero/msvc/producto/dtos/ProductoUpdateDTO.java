package com.ampuero.msvc.producto.dtos;

/**
 * ProductoUpdateDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para la actualización de productos existentes.
 * Este DTO permite actualizaciones parciales, donde todos los campos son opcionales.
 * Si un campo no se proporciona (null), no se modificará en la entidad existente.
 *
 * Uso común:
 * - Solicitudes PUT/PATCH para actualizar productos existentes.
 * - Actualizaciones parciales donde solo se modifican campos específicos.
 * - Validación de datos de entrada en actualizaciones.
 * - Separación clara entre datos de actualización y la entidad de dominio.
 *
 * Atributos:
 * - nombreProducto: Nuevo nombre del producto (opcional).
 * - descripcionProducto: Nueva descripción del producto (opcional).
 * - precioProducto: Nuevo precio del producto (opcional, debe ser positivo si se proporciona).
 * - activo: Nuevo estado de activación del producto (opcional).
 * - stock: Nueva cantidad en inventario (opcional).
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [25-06-25]
 * Última modificación: [25-06-25]
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la actualización de productos existentes")
public class ProductoUpdateDTO {

    @Schema(description = "Nuevo nombre del producto", example = "Laptop Gaming Asus ROG Actualizada")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreProducto;

    @Schema(description = "Nueva descripción del producto", example = "Laptop para gaming actualizada con procesador Intel i9, 32GB RAM, tarjeta gráfica RTX 4070")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcionProducto;

    @Schema(description = "Nuevo precio del producto en pesos chilenos", example = "1199990.99")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precioProducto;

    @Schema(description = "Nuevo estado de activación del producto", example = "true")
    private Boolean activo;

    @Schema(description = "Nueva cantidad en inventario", example = "15")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
} 