package com.ampuero.msvc.producto.assemblers;

import com.ampuero.msvc.producto.controllers.ProductoControllerV2;
import com.ampuero.msvc.producto.models.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>>{

    @Override
    public EntityModel<Producto> toModel(Producto entity){
        return EntityModel.of(
          entity,
          linkTo(methodOn(ProductoControllerV2.class).traerPorId(entity.getIdProducto())).withSelfRel(),
          linkTo(methodOn(ProductoControllerV2.class).traerTodos()).withRel("productos")
        );
    }
}
