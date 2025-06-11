package com.ampuero.msvc.clientes.services;

import com.ampuero.msvc.clientes.clients.BoletaClientRest;
import com.ampuero.msvc.clientes.models.Cliente;
import com.ampuero.msvc.clientes.repositories.ClienteRepository;
import net.datafaker.Faker;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    Faker faker = new Faker(Locale.of("es", "CL"));

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private BoletaClientRest boletaClientRest;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private List<Cliente> clienteList = new ArrayList<>();

    private Cliente clientePrueba;

    private String generateCustomEmail() {
        String nombre = faker.name().username().replaceAll("[^a-zA-Z0-9]", "");
        return nombre + "@duocuc.cl";
    }

    @BeforeEach
    public void setUp(){

        for(int i=0;i<100;i++){
            Cliente cliente = new Cliente();
            cliente.setIdUsuario((long) i+1);
            cliente.setNombreCliente(faker.name().firstName());
            cliente.setApellidoCliente(faker.name().lastName());
            cliente.setCorreoCliente(generateCustomEmail());
            cliente.setContraseniaCliente(faker.expression("#{bothify '??##!!??##'}"));
            cliente.setDireccionEnvioCliente(faker.address().fullAddress());
            this.clienteList.add(cliente);
        }
        this.clientePrueba = this.clienteList.get(0);
    }


}
