package com.ampuero.msvc.detalle.models;

/**
 * BoletaPojo.java
 *
 * Descripción: Clase POJO que representa la estructura básica de una boleta
 * utilizada para intercambiar datos entre microservicios, especialmente entre el microservicio de detalle
 * y el microservicio de boleta.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 *
 * Funcionalidad:
 * - Encapsula la información principal de una boleta, incluyendo el ID, fecha de emisión,
 *   total, descripción y los datos del cliente asociado.
 * - Esta clase es utilizada como un objeto de transferencia (DTO) para evitar acoplamiento directo
 *   con las entidades del microservicio externo.
 * - Se usa normalmente cuando se recibe o envía información desde/hacia otros servicios vía Feign.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Clase que representa un objeto boleta simple utilizado para la comunicación entre microservicios.
 * Contiene información general de la boleta y del cliente asociado.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoletaPojo {
    private Long idBoleta;
    private Date fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClientePojo cliente;
}