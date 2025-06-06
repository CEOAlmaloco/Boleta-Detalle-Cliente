package com.ampuero.msvc.clientes.init;

import com.ampuero.msvc.clientes.repositories.ClienteRepository;
import com.ampuero.msvc.clientes.models.Cliente;
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
    private ClienteRepository clienteRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);


    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("es","CL"));

        if(clienteRepository.count()==0){
            for(int i=0;i<1000;i++){
                Cliente cliente = new Cliente();

                cliente.setNombreCliente(faker.name().firstName()); // Nombre aleatorio
                cliente.setApellidoCliente(faker.name().lastName()); // Apellido aleatorio

                String correo = (cliente.getNombreCliente().toLowerCase() + "." +
                        cliente.getApellidoCliente().toLowerCase() +
                        faker.number().numberBetween(1, 100) + "@mail.com")
                        .replace(" ", "").replace("'", "");
                cliente.setCorreoCliente(correo);

                cliente.setContraseniaCliente(faker.internet().password(8, 16, true, true, true));

                cliente.setDireccionEnvioCliente(faker.address().fullAddress());


                logger.info("El nombre que agregas es {}", cliente.getNombreCliente());
                cliente = clienteRepository.save(cliente);
                logger.info("EL cliente creado es: {}", cliente);

            }
        }

    }
}
