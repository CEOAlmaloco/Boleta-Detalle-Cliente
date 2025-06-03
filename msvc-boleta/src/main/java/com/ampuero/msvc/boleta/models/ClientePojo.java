package com.ampuero.msvc.boleta.models;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Data
public class ClientePojo {
    private Long idUsuario;
    private String nombreCliente;
    private String correoCliente;
}