package com.ampuero.msvc.detalle.assemblers;

import com.ampuero.msvc.detalle.controllers.DetalleControllerV2;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.Link;

/**
 * Assembler para convertir DTOs de respuesta de Detalle en EntityModel con enlaces HATEOAS.
 * 
 * Este assembler se enfoca en respuestas enriquecidas que incluyen información de boleta y producto,
 * proporcionando enlaces contextuales para navegación en la API RESTful.
 */
@Component
public class DetalleResponseDTOModelAssembler implements RepresentationModelAssembler<DetalleResponseDTO, EntityModel<DetalleResponseDTO>> {

    @Override
    public EntityModel<DetalleResponseDTO> toModel(DetalleResponseDTO entity) {
        return EntityModel.of(
                entity,
                // 1. Self - GET al detalle específico  
                linkTo(methodOn(DetalleControllerV2.class).obtenerPorId(entity.getIdDetalle())).withSelfRel(),
                
                // 2. Producto - GET al microservicio de productos
                Link.of("http://localhost:8082/api/v2/productos/" + entity.getProducto().getIdProducto()).withRel("producto"),
                
                // 3. Boleta - GET al microservicio de boletas  
                Link.of("http://localhost:8081/api/v2/boletas/" + entity.getBoleta().getIdBoleta()).withRel("boleta"),
                
                // 4. Cliente - GET al microservicio de clientes (a través de la boleta)
                Link.of("http://localhost:8080/api/v2/clientes/" + entity.getBoleta().getCliente().getIdUsuario()).withRel("cliente"),
                
                // 5. Detalles-boleta - GET a todos los detalles de esta boleta
                linkTo(methodOn(DetalleControllerV2.class).obtenerPorBoleta(entity.getBoleta().getIdBoleta())).withRel("detalles-boleta")
        );
    }
} 