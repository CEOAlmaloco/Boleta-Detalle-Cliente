package com.ampuero.msvc.clientes.services;

import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
import com.ampuero.msvc.clientes.models.Cliente;

import java.util.List;

public interface ClienteService {
    List<Cliente> traerTodos();
    Cliente traerPorId(Long id);
    Cliente crearCliente(ClienteCreationDTO clienteDetails);
    void eliminarCliente(Long id);
    Cliente actualizarCliente(Long id, Cliente cliente);
}
