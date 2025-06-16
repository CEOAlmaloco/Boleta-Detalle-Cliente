package com.ampuero.msvc.boleta.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta") //Anotaciones para JPA
    private Long idBoleta;

    @Column(name = "fecha_emision_boleta")
    private LocalDate fechaEmisionBoleta = LocalDate.now();

    @Column(name = "total_boleta")
    private double totalBoleta;

    @Column(nullable = false, name = "descripcion_boleta")
    private String descripcionBoleta;

    @Column(nullable = false, name = "id_cliente_pojo")
    private Long idClientePojo;
}



