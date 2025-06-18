package com.ampuero.msvc.detalle.models;
/**
 * ClientePojo.java
 *
 * Descripción: Clase POJO que representa la estructura simplificada de un cliente (usuario)
 * utilizada para la transferencia de datos entre microservicios, especialmente entre el microservicio
 * de detalle y el microservicio de usuarios.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 *
 * Funcionalidad:
 * - Define los atributos esenciales del cliente asociados a una boleta o transacción.
 * - Se utiliza como un DTO (Data Transfer Object) para desacoplar los servicios.
 * - Permite transportar información del cliente sin exponer toda la entidad original.
 */
import lombok.*;

/**
 * Clase que encapsula los datos básicos del cliente (usuario).
 * Utilizada para la comunicación entre microservicios mediante Feign u otros mecanismos de intercambio de datos.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Data
public class ClientePojo {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
}