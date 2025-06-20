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

/**
 * Inicializador de datos simple que crea detalles directamente en la base de datos
 * sin validar dependencias externas (boletas/productos).
 * 
 * Esto es útil para:
 * - Desarrollo local
 * - Testing de HATEOAS
 * - Pruebas manuales con Postman
 */
@Profile({"dev", "default"})
@Component
public class LoadDatabaseSimple implements CommandLineRunner {

    @Autowired
    private DetalleRepository detalleRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabaseSimple.class);

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("es", "CL"));

        if (detalleRepository.count() == 0) {
            logger.info("Generando 100 detalles fake para desarrollo...");
            
            // Generar detalles más distribuidos por boleta
            for (int i = 0; i < 100; i++) {
                Detalle detalle = new Detalle();

                // Distribución más balanceada: cada boleta tendrá aprox. 20 detalles
                Long idBoleta = (long) ((i / 20) + 1); // Boletas 1-5, cada una con ~20 detalles
                detalle.setIdBoletaPojo(idBoleta);
                detalle.setIdProductoPojo(faker.number().numberBetween(1L, 10L));
                detalle.setCantidadDetalle(faker.number().numberBetween(1, 10));

                double precioUnitario = faker.number().randomDouble(2, 10, 1000);
                detalle.setPrecioUnitarioDetalle(precioUnitario);
                detalle.setSubtotalDetalle(detalle.getCantidadDetalle() * precioUnitario);

                detalleRepository.save(detalle);
                
                if (i % 20 == 0) {
                    logger.info("Creados {} detalles... (Boleta actual: {})", i + 1, idBoleta);
                }
            }
            
            logger.info("LoadDatabase completado: {} detalles creados distribuidos en 5 boletas", detalleRepository.count());
        } else {
            logger.info("Base de datos ya contiene {} detalles, saltando LoadDatabase", detalleRepository.count());
        }
    }
} 