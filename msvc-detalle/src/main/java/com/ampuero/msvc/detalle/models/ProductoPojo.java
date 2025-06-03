package com.ampuero.msvc.detalle.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPojo {
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioProducto;
}