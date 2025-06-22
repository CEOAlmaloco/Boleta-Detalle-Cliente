package com.ampuero.msvc.clientes.repositories;

import com.ampuero.msvc.clientes.models.Cliente;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCorreoClienteAndContraseniaCliente(@NotBlank(message = "El campo correo no puede estar Vacio") String correoCliente, @NotBlank(message = "El campo contraseña no puede estar Vacio") String contraseniaCliente);
}
