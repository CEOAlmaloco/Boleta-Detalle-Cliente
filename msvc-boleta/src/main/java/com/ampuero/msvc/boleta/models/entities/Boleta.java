package com.ampuero.msvc.boleta.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "boletas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta") //Anotaciones para JPA
    private Long idBoleta;

    private Date fechaEmisionBoleta = new Date();

    private double totalBoleta;

    @Column(nullable = false, name = "descripcion_boleta")
    private String descripcionBoleta;

    @Column(nullable = false, name = "id_cliente_pojo")
    private Long idClientePojo;
}



