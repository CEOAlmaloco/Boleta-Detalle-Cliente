package com.ampuero.msvc.detalle.assemblers;

import com.ampuero.msvc.detalle.controllers.DetalleControllerV2;
import com.ampuero.msvc.detalle.models.entities.Detalle;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.Link;

/**
 * Assembler simple para Detalle siguiendo exactamente el patrón de msvc-medicos.
 * 
 * Similar a MedicoModelAssembler, este assembler proporciona solo los enlaces esenciales:
 * - self: enlace al recurso específico
 * - collection: enlace a la colección de detalles
 * - producto: enlace al producto relacionado (equivalente al link comentado en MedicoModelAssembler)
 */
@Component
public class DetalleSimpleAssembler implements RepresentationModelAssembler<Detalle, EntityModel<Detalle>> {

    @Override
    public EntityModel<Detalle> toModel(Detalle entity) {
        // Siguiendo exactamente el patrón de MedicoModelAssembler
        return EntityModel.of(
                entity,
                linkTo(methodOn(DetalleControllerV2.class).obtenerPorId(entity.getIdDetalle())).withSelfRel(),
                linkTo(methodOn(DetalleControllerV2.class).obtenerTodos()).withRel("detalles")
                // Enlace a producto (equivalente al link comentado en MedicoModelAssembler)
                // Link.of("http://localhost:8082/api/v2/productos/" + entity.getIdProductoPojo()).withRel("producto")
        );
    }
} 