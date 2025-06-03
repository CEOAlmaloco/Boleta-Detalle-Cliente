package com.ampuero.msvc.boleta.dtos;

import com.ampuero.msvc.boleta.models.ClientePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// En msvc-boletas
@Data
public class BoletaResponseDTO {
    private Long idBoleta;
    private Date fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClienteResponseDTO cliente;}