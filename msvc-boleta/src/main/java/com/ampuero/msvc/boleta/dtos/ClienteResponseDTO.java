package com.ampuero.msvc.boleta.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta con datos de Cliente")
public class ClienteResponseDTO {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
}