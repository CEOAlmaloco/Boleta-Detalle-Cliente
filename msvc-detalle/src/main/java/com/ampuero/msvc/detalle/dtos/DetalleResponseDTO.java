package com.ampuero.msvc.detalle.dtos;

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