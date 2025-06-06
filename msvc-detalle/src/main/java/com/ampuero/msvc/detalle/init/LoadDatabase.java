package com.ampuero.msvc.detalle.init;

import com.ampuero.msvc.detalle.models.entities.Detalle;
import com.ampuero.msvc.detalle.repositories.DetalleRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Profile("dev")
@Component
public class LoadDatabase implements CommandLineRunner {

    @Autowired
    private DetalleRepository detalleRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);


    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("es","CL"));

        if(detalleRepository.count()==0){
            for(int i=0;i<1000;i++){
                Detalle detalle = new Detalle();


                detalle.setIdBoletaPojo(faker.number().randomNumber()); // ID de boleta aleatorio
                detalle.setIdProductoPojo(faker.number().randomNumber()); // ID de producto aleatorio
                detalle.setCantidadDetalle(faker.number().numberBetween(1, 100)); // Cantidad entre 1 y 100


                double precioUnitario = faker.number().randomDouble(2, 1, 10000);
                detalle.setPrecioUnitarioDetalle(precioUnitario);


                detalle.setSubtotalDetalle(detalle.getCantidadDetalle() * precioUnitario);

                logger.info("El detalle que agregas es {}", detalle.getPrecioUnitarioDetalle());


                detalle = detalleRepository.save(detalle);

                logger.info("EL detalle creado es: {}", detalle);

            }
        }

    }
}
