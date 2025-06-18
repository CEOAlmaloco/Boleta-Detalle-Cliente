package com.ampuero.msvc.detalle.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO detalle")

public class DetalleDTO {
    @Schema(description = "Tiene que tener una ID asociada a una boleta existente", example = "1")
    private Long idBoletaPojo;
    @Schema(description = "Tiene que tener una ID asociada a un producto existente", example = "1")
    private Long idProductoPojo;
    @Schema(description = "Cantidad de detalle", example = "1")
    private Integer cantidadDetalle;

}

