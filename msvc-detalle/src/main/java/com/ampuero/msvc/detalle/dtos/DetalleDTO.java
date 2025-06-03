package com.ampuero.msvc.detalle.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dtos/DetalleBoletaDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDTO {
    private Long idBoletaPojo;
    private Long idProductoPojo;
    private Integer cantidadDetalle;

}

