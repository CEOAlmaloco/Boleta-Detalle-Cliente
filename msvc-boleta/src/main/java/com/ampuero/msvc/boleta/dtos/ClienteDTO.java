package com.ampuero.msvc.boleta.dtos;

import lombok.*;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
}
