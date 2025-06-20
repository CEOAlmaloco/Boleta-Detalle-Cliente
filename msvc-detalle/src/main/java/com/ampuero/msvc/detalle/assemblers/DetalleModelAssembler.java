package com.ampuero.msvc.detalle.assemblers;

import com.ampuero.msvc.detalle.controllers.DetalleControllerV2;
import com.ampuero.msvc.detalle.models.entities.Detalle;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.Link;

/**
 * Assembler para convertir entidades Detalle en EntityModel con enlaces HATEOAS.
 * 
 * Este assembler proporciona 5 tipos de enlaces para cumplir con los principios RESTful:
 * 1. self - enlace al propio recurso
 * 2. boleta - enlace a la boleta asociada
 * 3. detalles - enlace a todos los detalles 
 * 4. update - enlace para actualizar el detalle
 * 5. delete - enlace para eliminar el detalle
 */
@Component
public class DetalleModelAssembler implements RepresentationModelAssembler<Detalle, EntityModel<Detalle>> {

    @Override
    public EntityModel<Detalle> toModel(Detalle entity) {
        return EntityModel.of(
                entity,
                // 1. Self - GET al detalle específico
                linkTo(methodOn(DetalleControllerV2.class).obtenerPorId(entity.getIdDetalle())).withSelfRel(),
                
                // 2. Producto - GET al microservicio de productos
                Link.of("http://localhost:8082/api/v2/productos/" + entity.getIdProductoPojo()).withRel("producto"),
                
                // 3. Boleta - GET al microservicio de boletas
                Link.of("http://localhost:8081/api/v2/boletas/" + entity.getIdBoletaPojo()).withRel("boleta"),
                
                // 4. Detalles-boleta - GET a todos los detalles de esta boleta
                linkTo(methodOn(DetalleControllerV2.class).obtenerPorBoleta(entity.getIdBoletaPojo())).withRel("detalles-boleta"),
                
                // 5. Detalles - GET a todos los detalles del sistema
                linkTo(methodOn(DetalleControllerV2.class).obtenerTodos()).withRel("detalles")
        );
    }
} 