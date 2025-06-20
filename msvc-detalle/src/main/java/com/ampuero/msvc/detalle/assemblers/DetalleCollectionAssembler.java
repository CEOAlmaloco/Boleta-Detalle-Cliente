package com.ampuero.msvc.detalle.assemblers;

import com.ampuero.msvc.detalle.controllers.DetalleControllerV2;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.Link;

/**
 * Assembler para convertir colecciones de DetalleResponseDTO en CollectionModel con enlaces HATEOAS.
 * 
 * Este assembler maneja operaciones de colección proporcionando enlaces para:
 * 1. self - enlace a la colección actual
 * 2. create - enlace para crear nuevos detalles
 * 3. search - enlace para búsquedas por boleta
 * 4. root - enlace a la raíz de la API
 * 5. refresh - enlace para refrescar la colección
 */
@Component
public class DetalleCollectionAssembler {

    private final DetalleResponseDTOModelAssembler detalleAssembler;

    public DetalleCollectionAssembler(DetalleResponseDTOModelAssembler detalleAssembler) {
        this.detalleAssembler = detalleAssembler;
    }

    /**
     * Convierte una lista de DetalleResponseDTO en CollectionModel con enlaces HATEOAS.
     * 
     * @param detalles Lista de detalles a convertir
     * @return CollectionModel con enlaces de navegación
     */
    public CollectionModel<EntityModel<DetalleResponseDTO>> toCollectionModel(List<DetalleResponseDTO> detalles) {
        List<EntityModel<DetalleResponseDTO>> detalleModels = detalles.stream()
                .map(detalleAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(detalleModels)
                // 1. Self - GET a la colección actual
                .add(linkTo(methodOn(DetalleControllerV2.class).obtenerTodos()).withSelfRel())
                
                // 2. Search - GET para búsquedas por boleta (template)
                .add(linkTo(DetalleControllerV2.class).slash("boleta").slash("{idBoleta}").withRel("buscar-por-boleta"))
                
                // 3. Productos - GET al microservicio de productos
                .add(Link.of("http://localhost:8082/api/v2/productos").withRel("productos"))
                
                // 4. Boletas - GET al microservicio de boletas
                .add(Link.of("http://localhost:8081/api/v2/boletas").withRel("boletas"))
                
                // 5. Clientes - GET al microservicio de clientes  
                .add(Link.of("http://localhost:8080/api/v2/clientes").withRel("clientes"));
    }

    /**
     * Convierte una lista de detalles de una boleta específica en CollectionModel.
     * 
     * @param detalles Lista de detalles de la boleta
     * @param idBoleta ID de la boleta para enlaces contextuales
     * @return CollectionModel con enlaces específicos para la boleta
     */
    public CollectionModel<EntityModel<DetalleResponseDTO>> toCollectionModelForBoleta(
            List<DetalleResponseDTO> detalles, Long idBoleta) {
        
        List<EntityModel<DetalleResponseDTO>> detalleModels = detalles.stream()
                .map(detalleAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(detalleModels)
                // Enlaces GET específicos para detalles de una boleta
                .add(linkTo(methodOn(DetalleControllerV2.class).obtenerPorBoleta(idBoleta)).withSelfRel())
                .add(Link.of("http://localhost:8081/api/v2/boletas/" + idBoleta).withRel("boleta"))
                .add(linkTo(methodOn(DetalleControllerV2.class).obtenerTodos()).withRel("todos-los-detalles"))
                .add(Link.of("http://localhost:8082/api/v2/productos").withRel("productos"))
                .add(Link.of("http://localhost:8080/api/v2/clientes").withRel("clientes"));
    }
} 