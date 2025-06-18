package com.ampuero.msvc.detalle.dtos;

/**
 * MontoUpdateRequestDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) utilizado para representar una solicitud de actualización
 * del monto total de una boleta o factura. Este DTO es enviado desde el microservicio de detalle
 * hacia el microservicio de boleta para incrementar o decrementar el total acumulado.
 *
 * Uso común:
 * - Actualización del total de la boleta cuando se crea, actualiza o elimina un detalle.
 * - Comunicación entre microservicios mediante clientes Feign.
 *
 * Validación:
 * - El campo 'monto' es obligatorio y no puede ser nulo, validado con "@NotNull".
 *
 * Atributos:
 * - monto: Valor que se desea sumar (positivo) o restar (negativo) al total de la boleta.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 */

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MontoUpdateRequestDTO {
    @NotNull
    private Double monto;
} 