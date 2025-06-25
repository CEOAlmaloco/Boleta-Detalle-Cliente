package com.ampuero.msvc.producto.assemblers;

/**
 * ProductoResponseDTOModelAssembler.java
 *
 * Descripción:
 * Assembler especializado para convertir DTOs de respuesta de Producto en EntityModel 
 * con enlaces HATEOAS. Este assembler se enfoca en respuestas GET enriquecidas que incluyen
 * metadatos adicionales y navegación contextual para la API RESTful.
 *
 * Funcionalidades:
 * - Conversión de ProductoResponseDTO a EntityModel con enlaces HATEOAS.
 * - Enlaces contextuales basados en el estado del producto.
 * - Referencias cruzadas a microservicios relacionados.
 * - Navegación avanzada para operaciones específicas.
 *
 * Enlaces proporcionados:
 * - self: Enlace GET al producto específico.
 * - productos: Enlace GET a la colección de productos.
 * - update: Enlace para actualizar el producto.
 * - delete: Enlace para eliminar el producto (solo si está activo).
 * - detalles: Enlace GET a detalles que incluyen este producto.
 * - inventario: Enlace GET a información de inventario (si aplica).
 * - estadisticas: Enlace GET a estadísticas del producto.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [25-06-25]
 * Última modificación: [25-06-25]
 */

import com.ampuero.msvc.producto.controllers.ProductoControllerV2;
import com.ampuero.msvc.producto.dtos.ProductoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductoResponseDTOModelAssembler implements RepresentationModelAssembler<ProductoResponseDTO, EntityModel<ProductoResponseDTO>> {

    @Override
    public EntityModel<ProductoResponseDTO> toModel(ProductoResponseDTO entity) {
        EntityModel<ProductoResponseDTO> model = EntityModel.of(
                entity,
                // 1. Self - GET al producto específico
                linkTo(methodOn(ProductoControllerV2.class).traerPorId(entity.getIdProducto())).withSelfRel(),
                
                // 2. Productos - GET a la colección de productos
                linkTo(methodOn(ProductoControllerV2.class).traerTodos()).withRel("productos"),
                
                // 3. Update - PUT para actualizar el producto
                linkTo(methodOn(ProductoControllerV2.class).actualizarProducto(entity.getIdProducto(), null)).withRel("update"),
                
                // 4. Detalles - GET a detalles que incluyen este producto (microservicio detalle)
                Link.of("http://localhost:8083/api/v2/detalles/producto/" + entity.getIdProducto()).withRel("detalles-producto"),
                
                // 5. V1 API - Enlace GET a la versión 1 de la API para compatibilidad
                Link.of("http://localhost:8082/api/v1/productos/" + entity.getIdProducto()).withRel("v1-api")
        );

        // Enlaces condicionales basados en el estado del producto
        if (entity.getActivo() != null && entity.getActivo()) {
            // 6. Delete - Solo disponible si el producto está activo
            model.add(linkTo(methodOn(ProductoControllerV2.class).eliminarProducto(entity.getIdProducto())).withRel("delete"));
            
            // 7. Comprar - Enlace conceptual a un proceso de compra
            model.add(Link.of("http://localhost:8084/api/v1/compras/producto/" + entity.getIdProducto()).withRel("comprar"));
        }

        // Enlaces adicionales si hay información de stock
        if (entity.getStock() != null) {
            // 8. Inventario - Enlace GET a gestión de inventario
            model.add(Link.of("http://localhost:8085/api/v1/inventario/producto/" + entity.getIdProducto()).withRel("inventario"));
            
            if (entity.getStock() > 0) {
                // 9. Disponible - GET que indica que el producto está disponible para venta
                model.add(Link.of("http://localhost:8084/api/v1/disponibilidad/producto/" + entity.getIdProducto()).withRel("disponible"));
            } else {
                // 10. Sin-stock - Enlace a restock del producto
                model.add(Link.of("http://localhost:8085/api/v1/restock/producto/" + entity.getIdProducto()).withRel("restock"));
            }
        }

        // 11. Estadísticas - Enlace GET a estadísticas del producto
        model.add(Link.of("http://localhost:8086/api/v1/estadisticas/producto/" + entity.getIdProducto()).withRel("estadisticas"));

        return model;
    }
} 