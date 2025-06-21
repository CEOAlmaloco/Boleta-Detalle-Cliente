package com.ampuero.msvc.detalle.dtos;

/**
 * BoletaEnDetalleDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) que representa la información básica de una boleta
 * dentro del contexto de un detalle. Este DTO se utiliza para incrustar información relevante
 * de una boleta en respuestas relacionadas con detalles de boleta, permitiendo una visualización
 * resumida de la boleta asociada.
 *
 * Uso común:
 * - Incluir información de la boleta en las respuestas del microservicio de detalle.
 * - Facilitar la desnormalización de datos sin requerir la consulta directa al microservicio de boletas.
 *
 * Atributos:
 * - idBoleta: Identificador único de la boleta.
 * - fechaEmisionBoleta: Fecha en la que se emitió la boleta.
 * - totalBoleta: Monto total de la boleta, incluyendo todos los detalles asociados.
 * - descripcionBoleta: Descripción textual u observaciones adicionales sobre la boleta.
 * - cliente: Información resumida del cliente asociado a la boleta (representado por ClienteEnBoletaDTO).
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaEnDetalleDTO {
    private Long idBoleta; // Anteriormente idFactura
    private LocalDate fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClienteEnBoletaDTO cliente;
} 