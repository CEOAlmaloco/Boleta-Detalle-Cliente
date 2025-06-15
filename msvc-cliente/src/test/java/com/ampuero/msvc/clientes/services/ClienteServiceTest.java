package com.ampuero.msvc.clientes.services;

import com.ampuero.msvc.clientes.clients.BoletaClientRest;
import com.ampuero.msvc.clientes.exceptions.ClienteException;
import com.ampuero.msvc.clientes.models.Cliente;
import com.ampuero.msvc.clientes.repositories.ClienteRepository;
import net.datafaker.Faker;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    private String generarMailPersonalizado() {
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
            cliente.setCorreoCliente(generarMailPersonalizado());
            cliente.setContraseniaCliente(faker.expression("#{bothify '??##!!??##'}"));
            cliente.setDireccionEnvioCliente(faker.address().fullAddress());
            this.clienteList.add(cliente);
        }
        this.clientePrueba = this.clienteList.get(0);
    }

    @Test
    @DisplayName("Devuelve todos los clientes")
    public void debeListarTodosLosClientes(){
        when(clienteRepository.findAll()).thenReturn(this.clienteList);
        List<Cliente> resultado = clienteService.traerTodos();
        assertThat(resultado).hasSize(200);
        assertThat(resultado).contains(this.clientePrueba);

        verify(clienteRepository, times(1)).findAll();


    }

    @Test
    @DisplayName("Encontrar por id un cliente")
    public void encontrarPorIdUnCliente(){
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePrueba));
        Cliente resultado = clienteService.traerPorId(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(this.clientePrueba);

        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Actualizar un cliente correctamente")
    public void debeActualizarCliente(){

        Cliente nuevosDatos = new Cliente();

        nuevosDatos.setNombreCliente(faker.name().firstName());
        nuevosDatos.setApellidoCliente(faker.name().lastName());
        nuevosDatos.setCorreoCliente(generarMailPersonalizado());
        nuevosDatos.setContraseniaCliente(faker.expression("#{bothify '??##??##'}"));
        nuevosDatos.setDireccionEnvioCliente(faker.address().fullAddress());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePrueba));
        //Responde devolviendo exactamente el mismo el mismo objeto que se paso como argumento
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Cliente resultado = clienteService.actualizarCliente(1L, nuevosDatos);

        //Assert
        assertThat(resultado.getNombreCliente()).isEqualTo(nuevosDatos.getNombreCliente());
        assertThat(resultado.getApellidoCliente()).isEqualTo(nuevosDatos.getApellidoCliente());
        assertThat(resultado.getCorreoCliente()).isEqualTo(nuevosDatos.getCorreoCliente());
        assertThat(resultado.getContraseniaCliente()).isEqualTo(nuevosDatos.getContraseniaCliente());
        assertThat(resultado.getDireccionEnvioCliente()).isEqualTo(nuevosDatos.getDireccionEnvioCliente());

        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clientePrueba);
    }

    @Test
    @DisplayName("Lanza excepcion si cliente no existe al actualizar")
    public void lanzaExcepcionSiClienteNoExiste(){
        Cliente nuevosDatos = new Cliente();

        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());


        /*Aca se valida que cuando se intenta actualizar un cliente que no existe, el servicio
        * Lanza una excepcion.
        * Da un mensaje que incluye el id del cliente no encontrado.
        * */
        assertThatThrownBy(() -> clienteService.actualizarCliente(999L, nuevosDatos))
            .isInstanceOf(ClienteException.class)
            .hasMessageContaining("Cliente con id 999 no existe");

        verify(clienteRepository, times(1)).findById(999L);
        verify(clienteRepository, never()).save(any()); //Metodo save no debe ser ejecutado ni una vez
    }

    @Test
    @DisplayName("Eliminar cliente existente correctamente")
    public void debeEliminarCliente(){

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePrueba));

        clienteService.eliminarCliente(1L);

        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Lanza excepcion si cliente no existe al eliminar")
    public void lanzaExcepcionSiClienteNoExisteAlEliminar(){

        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(999L))
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("Cliente con id 999 no existe");

        verify(clienteRepository, never()).deleteById(999L);
    }
}
