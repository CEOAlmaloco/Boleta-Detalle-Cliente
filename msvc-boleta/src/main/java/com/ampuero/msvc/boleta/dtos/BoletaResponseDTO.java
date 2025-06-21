package com.ampuero.msvc.boleta.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que incluye todos los datos de la boleta y del cliente asociado.
 * Se utiliza en las operaciones de consulta para proporcionar información completa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta completa con datos de boleta y cliente")
public class BoletaResponseDTO {

    @Schema(description = "ID único de la boleta", example = "1")
    @JsonProperty("id_boleta")
    private Long idBoleta;

    @Schema(description = "Fecha de emisión de la boleta")
    @JsonProperty("fecha_emision")
    private LocalDate fechaEmisionBoleta;

    @Schema(description = "Total acumulado de la boleta", example = "1250.50")
    @JsonProperty("total_boleta")
    private Double totalBoleta;

    @Schema(description = "Descripción de la boleta")
    @JsonProperty("descripcion_boleta")
    private String descripcionBoleta;

    @Schema(description = "Información completa del cliente asociado")
    @JsonProperty("cliente")
    private ClienteResponseDTO cliente;
}