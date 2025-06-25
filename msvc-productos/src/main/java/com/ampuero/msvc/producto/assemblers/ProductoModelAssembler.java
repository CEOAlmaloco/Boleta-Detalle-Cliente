package com.ampuero.msvc.producto.assemblers;

/**
 * ProductoModelAssembler.java
 *
 * Descripción:
 * Assembler para convertir entidades Producto en EntityModel con enlaces HATEOAS.
 * Proporciona navegación RESTful enriquecida para operaciones GET de productos, incluyendo
 * enlaces a operaciones relacionadas y recursos externos.
 *
 * Funcionalidades:
 * - Conversión de entidades Producto a EntityModel con enlaces HATEOAS.
 * - Enlaces a operaciones GET del mismo producto.
 * - Enlaces a colecciones y recursos relacionados.
 * - Enlaces a microservicios externos (detalle) cuando corresponde.
 *
 * Enlaces proporcionados:
 * - self: Enlace GET al producto específico.
 * - productos: Enlace GET a la colección de productos.
 * - update: Enlace para actualizar el producto.
 * - delete: Enlace para eliminar el producto.
 * - detalles: Enlace GET a detalles que usan este producto.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [25-06-25]
 */

import com.ampuero.msvc.producto.controllers.ProductoControllerV2;
import com.ampuero.msvc.producto.models.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto entity) {
        return EntityModel.of(
                entity,
                // 1. Self - GET al producto específico
                linkTo(methodOn(ProductoControllerV2.class).traerPorId(entity.getIdProducto())).withSelfRel(),
                
                // 2. Productos - GET a la colección de productos
                linkTo(methodOn(ProductoControllerV2.class).traerTodos()).withRel("productos"),
                
                // 3. Update - PUT para actualizar el producto
                linkTo(methodOn(ProductoControllerV2.class).actualizarProducto(entity.getIdProducto(), null)).withRel("update"),
                
                // 4. Delete - DELETE para eliminar el producto  
                linkTo(methodOn(ProductoControllerV2.class).eliminarProducto(entity.getIdProducto())).withRel("delete"),
                
                // 5. Detalles - GET a detalles que usan este producto (microservicio detalle)
                Link.of("http://localhost:8083/api/v2/detalles/producto/" + entity.getIdProducto()).withRel("detalles-producto"),
                
                // 6. V1 API - Enlace GET a la versión 1 de la API para compatibilidad
                Link.of("http://localhost:8082/api/v1/productos/" + entity.getIdProducto()).withRel("v1-api")
        );
    }
}
