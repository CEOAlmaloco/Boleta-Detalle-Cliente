package com.ampuero.msvc.detalle.models.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un detalle")

public class Detalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    @Schema(description = "Codigo del detalle", example = "1")
    private Long idDetalle;

    @Column(nullable = false)
    @Schema(description = "Codigo de boletaPojo", example = "1")
    private Long idBoletaPojo;

    @Column(nullable = false)
    @Schema(description = "Codigo de productoPojo", example = "1")
    private Long idProductoPojo;

    @Column(nullable = false)
    @Schema(description = "Cantidad multiplicada por el producto", example = "4")
    private Integer cantidadDetalle;

    @Column(nullable = false)
    @Schema(description = "Precio unitario el cual se setea en 0 y se actualiza", example = "1990.0")
    private Double precioUnitarioDetalle;

    @Schema(description = "Subtotaldetalle", example = "1.0")
    private Double subtotalDetalle;
}