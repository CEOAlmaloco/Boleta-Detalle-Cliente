package com.ampuero.msvc.clientes.services;

import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
import com.ampuero.msvc.clientes.exceptions.ClienteException;
import com.ampuero.msvc.clientes.models.Cliente;
import com.ampuero.msvc.clientes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    //POST
    @Transactional
    @Override
    public Cliente crearCliente(ClienteCreationDTO clienteDetails) {
        Cliente clienteEntity = new Cliente();
        clienteEntity.setNombreCliente(clienteDetails.getNombreCliente());
        clienteEntity.setApellidoCliente(clienteDetails.getApellidoCliente());
        clienteEntity.setCorreoCliente(clienteDetails.getCorreoCliente());
        clienteEntity.setContraseniaCliente(clienteDetails.getContraseniaCliente());
        clienteEntity.setDireccionEnvioCliente(clienteDetails.getDireccionEnvioCliente());
        return clienteRepository.save(clienteEntity);
    }

    //GET todos
    @Transactional(readOnly = true)
    @Override
    public List<Cliente> traerTodos() {
        return clienteRepository.findAll();
    }

    //GET id
    @Transactional(readOnly = true)
    @Override
    public Cliente traerPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new ClienteException("Cliente con id " + id + " no encontrado")
        );
    }

    //PUT id
    @Transactional @Override
    public Cliente actualizarCliente(Long idCliente, Cliente clienteDetails){
        return clienteRepository.findById(idCliente).map(cliente -> {
            cliente.setNombreCliente(clienteDetails.getNombreCliente());
            cliente.setApellidoCliente(clienteDetails.getApellidoCliente());
            cliente.setCorreoCliente(clienteDetails.getCorreoCliente());
            cliente.setContraseniaCliente(clienteDetails.getContraseniaCliente());
            cliente.setDireccionEnvioCliente(clienteDetails.getDireccionEnvioCliente());
            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new ClienteException("Cliente con id " + idCliente + " no encontrado"));
    }

    //DELETE id
    @Transactional @Override
    public void eliminarCliente(Long id){
        clienteRepository.deleteById(id);
    }


}
