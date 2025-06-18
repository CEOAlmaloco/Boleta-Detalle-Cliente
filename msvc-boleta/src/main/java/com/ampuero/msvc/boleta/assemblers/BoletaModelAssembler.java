package com.ampuero.msvc.boleta.assemblers;

import com.ampuero.msvc.boleta.models.entities.Boleta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BoletaModelAssembler implements RepresentationModelAssembler<Boleta, EntityModel<Boleta>> {

    @Override
    public EntityModel<Boleta> toModel(Boleta entity){
        return EntityModel.of(
                entity
        );
    }
}
