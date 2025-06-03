package com.ampuero.msvc.detalle.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @Column(nullable = false)
    private Long idBoletaPojo;

    @Column(nullable = false)
    private Long idProductoPojo;

    @Column(nullable = false)
    private Integer cantidadDetalle;

    @Column(nullable = false)
    private Double precioUnitarioDetalle;

    private Double subtotalDetalle;
}