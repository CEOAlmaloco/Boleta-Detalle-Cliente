package com.ampuero.msvc.boleta.dtos;

import lombok.Data;

import java.time.LocalDate;

// En msvc-boletas
@Data
public class BoletaResponseDTO {
    private Long idBoleta;
    private LocalDate fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClienteResponseDTO cliente;}