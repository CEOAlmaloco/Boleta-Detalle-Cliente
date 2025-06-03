package com.ampuero.msvc.detalle.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoletaPojo {
    private Long idBoleta;
    private Date fechaEmisionBoleta;
    private double totalBoleta;
    private String descripcionBoleta;
    private ClientePojo cliente;
}