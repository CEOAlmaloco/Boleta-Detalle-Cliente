package com.ampuero.msvc.boleta.dtos;

import lombok.Data;

@Data
public class ClienteResponseDTO {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
}