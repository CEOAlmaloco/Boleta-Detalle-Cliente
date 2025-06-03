package com.ampuero.msvc.detalle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEnBoletaDTO {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
} 