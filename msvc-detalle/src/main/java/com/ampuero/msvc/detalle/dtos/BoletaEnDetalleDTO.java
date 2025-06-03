package com.ampuero.msvc.detalle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaEnDetalleDTO {
    private Long idBoleta; // Anteriormente idFactura
    private Date fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClienteEnBoletaDTO cliente;
} 