package com.ampuero.msvc.detalle.models;

/**
 * ProductoPojo.java
 *
 * Descripción: Clase POJO que representa un producto de forma simplificada,
 * utilizada para el intercambio de datos entre microservicios, especialmente entre el microservicio
 * de detalle y el microservicio de productos.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 *
 * Funcionalidad:
 * - Contiene la información esencial de un producto asociada a un detalle de boleta.
 * - Sirve como un DTO para transportar datos entre microservicios
 *   sin necesidad de importar entidades completas ni generar acoplamiento.
 * - Se utiliza cuando se crea o visualiza un detalle de boleta que hace referencia a productos.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que encapsula los datos básicos de un producto, incluyendo su ID, nombre,
 * descripción y precio. Se utiliza para representar productos asociados a detalles de boleta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPojo {
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioProducto;
}