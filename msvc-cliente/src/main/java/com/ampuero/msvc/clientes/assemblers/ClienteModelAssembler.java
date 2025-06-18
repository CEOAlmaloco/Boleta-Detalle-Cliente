package com.ampuero.msvc.clientes.assemblers;

import com.ampuero.msvc.clientes.controllers.ClienteControllerV2;
import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
import com.ampuero.msvc.clientes.models.Cliente;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>>{

    @Override
    public EntityModel<Cliente> toModel(Cliente entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(ClienteControllerV2.class).traerCliente(entity.getIdUsuario())).withSelfRel(),
                linkTo(methodOn(ClienteControllerV2.class).traerTodos()).withRel("clientes")
        );
    }
}
