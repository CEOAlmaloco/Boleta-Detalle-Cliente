package com.ampuero.msvc.boleta.init;

import com.ampuero.msvc.boleta.models.entities.Boleta;
import com.ampuero.msvc.boleta.repositories.BoletaRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Profile("dev")
@Component
public class LoadDatabase implements CommandLineRunner {

    @Autowired
    private BoletaRepository boletaRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);


    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("es","CL"));

        if(boletaRepository.count()==0){
            for(int i=0;i<100;i++){
                Boleta boleta = new Boleta();

                boleta.setFechaEmisionBoleta(LocalDate.now().minusDays(faker.number().numberBetween(0, 90)));
                boleta.setTotalBoleta(faker.number().randomDouble(2, 1000, 50000)); // Total entre 1.000,00 y 50.000,00
                boleta.setDescripcionBoleta("Boleta por compra de " + faker.commerce().productName()); // Descripción con producto
                boleta.setIdClientePojo((long) faker.number().numberBetween(1, 50)); // ID de cliente entre 1 y 50


                logger.info("El nombre que agregas es {}", boleta.getDescripcionBoleta());
                boleta = boletaRepository.save(boleta);
                logger.info("EL boleta creado es: {}", boleta);

            }
        }

    }
}
