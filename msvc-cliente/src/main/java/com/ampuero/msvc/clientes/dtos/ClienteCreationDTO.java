package com.ampuero.msvc.clientes.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class ClienteCreationDTO {
    @NotBlank(message = "El campo nombre no puede estar Vacio")
    private String nombreCliente;
    @NotBlank(message = "El campo apellido no puede estar Vacio")
    private String apellidoCliente;
    @NotBlank(message = "El campo correo no puede estar Vacio")
    private String correoCliente;
    @NotBlank(message = "El campo contraseña no puede estar Vacio")
    private String contraseniaCliente;
    @NotBlank(message = "El campo direccion de envio no puede estar Vacio")
    private String direccionEnvioCliente;
}
