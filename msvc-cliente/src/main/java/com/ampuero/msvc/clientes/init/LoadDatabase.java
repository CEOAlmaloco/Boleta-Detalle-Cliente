package com.ampuero.msvc.clientes.init;

import com.ampuero.msvc.clientes.repositories.ClienteRepository;
import com.ampuero.msvc.clientes.models.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    Faker faker = new Faker(Locale.of("es","CL"));

    @Autowired
    private ClienteRepository clienteRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    private String generarMailPersonalizado() {
        String nombre = faker.name().username().replaceAll("[^a-zA-Z0-9]", "");
        return nombre + "@duocuc.cl";
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        //Borrar todos los clientes existentes y reiniciar la columna id a 1
        clienteRepository.deleteAll();
        logger.info("Todos los clientes anteriores han sido eliminados.");
        em.createNativeQuery("ALTER TABLE clientes ALTER COLUMN id_cliente RESTART WITH 1").executeUpdate();

        if(clienteRepository.count()==0){
            for(int i=0;i<100;i++){
                Cliente cliente = new Cliente();

                cliente.setNombreCliente(faker.name().firstName()); // Nombre aleatorio
                cliente.setApellidoCliente(faker.name().lastName()); // Apellido aleatorio
                cliente.setCorreoCliente(generarMailPersonalizado());

                cliente.setContraseniaCliente(faker.internet().password(8, 16, true, true, true));

                cliente.setDireccionEnvioCliente(faker.address().fullAddress());


                logger.info("El nombre que agregas es {}", cliente.getNombreCliente());
                cliente = clienteRepository.save(cliente);
                logger.info("EL cliente creado es: {}", cliente);

            }
        }

    }
}
