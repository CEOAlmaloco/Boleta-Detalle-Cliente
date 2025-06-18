package com.ampuero.msvc.detalle.dtos;

/**
 * ClienteEnBoletaDTO.java
 *
 * Descripción:
 * Objeto de transferencia de datos (DTO) que encapsula la información básica del cliente
 * asociado a una boleta. Este DTO permite representar al cliente de forma simplificada
 * en respuestas que integran detalles de boleta, sin necesidad de exponer toda la entidad de usuario.
 *
 * Uso común:
 * - Incluir datos del cliente en los detalles de una boleta.
 * - Facilitar la visualización de información clave del usuario en módulos relacionados.
 * - Reducir el acoplamiento entre microservicios manteniendo solo los campos necesarios.
 *
 * Atributos:
 * - idUsuario: Identificador único del cliente.
 * - nombreCliente: Nombre completo del cliente.
 * - correoCliente: Dirección de correo electrónico del cliente.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEnBoletaDTO {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
} 