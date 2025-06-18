package com.ampuero.msvc.detalle.dtos;

/**
 * DetalleResponseDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para representar la respuesta detallada
 * de un ítem dentro de una boleta. Este DTO combina información del detalle, la boleta asociada
 * y el producto involucrado, facilitando una respuesta completa para vistas o servicios externos.
 *
 * Uso común:
 * - Respuestas a solicitudes GET en el microservicio de detalles.
 * - Visualización de información consolidada de cada línea de detalle en facturas o boletas.
 * - Comunicación entre microservicios en estructuras desacopladas.
 *
 * Atributos:
 * - idDetalle: Identificador único del detalle.
 * - boleta: Objeto que contiene información resumida de la boleta a la que pertenece este detalle.
 * - producto: Producto asociado a este detalle, incluye nombre, descripción y precio.
 * - cantidadDetalle: Cantidad de productos incluidos en este ítem.
 * - precioUnitarioDetalle: Precio individual del producto al momento de generar el detalle.
 * - subtotalDetalle: Resultado de cantidad * precio unitario. Representa el costo parcial del ítem.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 */

import com.ampuero.msvc.detalle.models.ProductoPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dtos/DetalleBoletaResponseDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleResponseDTO {
    private Long idDetalle;
    private BoletaEnDetalleDTO boleta;
    private ProductoPojo producto;
    private Integer cantidadDetalle;
    private Double precioUnitarioDetalle;
    private Double subtotalDetalle;
}