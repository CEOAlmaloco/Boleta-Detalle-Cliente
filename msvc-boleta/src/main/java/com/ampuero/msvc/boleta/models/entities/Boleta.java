package com.ampuero.msvc.boleta.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta")
    private Long idBoleta;

    private Date fechaEmisionBoleta = new Date();
    private double totalBoleta;

    @Column(nullable = false)
    private String descripcionBoleta;

    @Column(nullable = false)
    private Long idClientePojo;
}



