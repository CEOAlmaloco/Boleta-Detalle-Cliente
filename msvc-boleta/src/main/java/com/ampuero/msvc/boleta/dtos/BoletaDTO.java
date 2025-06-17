package com.ampuero.msvc.boleta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para la creación y actualización de boletas.
 * Contiene las validaciones necesarias para garantizar la integridad de los datos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una boleta")
public class BoletaDTO {

    @Schema(description = "Descripción detallada de la boleta", example = "Compra de productos electrónicos")
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    @JsonProperty("descripcion_boleta")
    private String descripcionBoleta;

    @Schema(description = "ID del cliente asociado a la boleta", example = "123")
    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    @JsonProperty("id_cliente")
    private Long idClientePojo;
}