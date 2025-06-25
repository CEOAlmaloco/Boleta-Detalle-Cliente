package com.ampuero.msvc.producto.dtos;

/**
 * ProductoCreationDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para la creación de nuevos productos.
 * Este DTO contiene únicamente los campos necesarios para crear un producto, excluyendo
 * el ID que es generado automáticamente por la base de datos.
 *
 * Uso común:
 * - Solicitudes POST para crear nuevos productos.
 * - Validación de datos de entrada en la creación de productos.
 * - Separación clara entre datos de entrada y la entidad de dominio.
 *
 * Atributos:
 * - nombreProducto: Nombre descriptivo del producto (requerido).
 * - descripcionProducto: Descripción detallada del producto.
 * - precioProducto: Precio del producto en la moneda base del sistema (requerido, debe ser positivo).
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [25-06-25]
 * Última modificación: [25-06-25]
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la creación de productos")
public class ProductoCreationDTO {

    @Schema(description = "Nombre del producto", example = "Laptop Gaming Asus ROG")
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreProducto;

    @Schema(description = "Descripción detallada del producto", example = "Laptop para gaming con procesador Intel i7, 16GB RAM, tarjeta gráfica RTX 4060")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcionProducto;

    @Schema(description = "Precio del producto en pesos chilenos", example = "899990.99")
    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precioProducto;
} 