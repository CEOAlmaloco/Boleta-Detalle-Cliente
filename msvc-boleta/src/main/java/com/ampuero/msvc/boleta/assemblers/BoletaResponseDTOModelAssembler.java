package com.ampuero.msvc.boleta.assemblers;

import com.ampuero.msvc.boleta.controllers.BoletaControllerV2;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BoletaResponseDTOModelAssembler implements RepresentationModelAssembler<BoletaResponseDTO, EntityModel<BoletaResponseDTO>> {
    @Override
    public EntityModel<BoletaResponseDTO> toModel(BoletaResponseDTO entity){
        return EntityModel.of(
                entity,
                linkTo(methodOn(BoletaControllerV2.class).obtenerBoletaPorId(entity.getIdBoleta())).withSelfRel(),
                linkTo(methodOn(BoletaControllerV2.class).obtenerTodas()).withRel("boletas"),
                linkTo(methodOn(BoletaControllerV2.class).obtenerPorCliente(entity.getCliente().getIdUsuario())).withRel("boletas-medico")
        );
    }
}
