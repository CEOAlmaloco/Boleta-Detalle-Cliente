package com.ampuero.msvc.clientes.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idUsuario;

    @Column(nullable = false)
    private String nombreCliente;

    @Column(nullable = false)
    private String apellidoCliente;

    @Column(nullable = false, unique = true)
    private String correoCliente;

    @Column(nullable = false, unique = true)
    private String contraseniaCliente;

    @Column(nullable = false)
    private String direccionEnvioCliente;

    @Column(nullable = false)
    private Boolean activo = true;
}
