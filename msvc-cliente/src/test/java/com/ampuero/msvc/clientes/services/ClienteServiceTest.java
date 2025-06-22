package com.ampuero.msvc.clientes.services;

import com.ampuero.msvc.clientes.clients.BoletaClientRest;
import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

// Habilitamos la integracion de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    // Creamos un objeto Faker para poblar automaticamente las Tablas
    Faker faker = new Faker(Locale.of("es", "CL"));

    // Creamos diferentes MOCKS para las dependencias
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private BoletaClientRest boletaClientRest;

    // Le ordenamos a Moclito que cree una instancia clienteService
    // e inyecte los mocks de arriba.
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
    @DisplayName("Debe crear cliente si no existe previamente con mismo correo y contraseña")
    void debeCrearClienteCuandoNoExisteDuplicado() {

        ClienteCreationDTO dto = new ClienteCreationDTO();
        dto.setNombreCliente("Juan");
        dto.setApellidoCliente("Pérez");
        dto.setCorreoCliente("juan@example.com");
        dto.setContraseniaCliente("123456");
        dto.setDireccionEnvioCliente("Calle Falsa 123");

        when(clienteRepository.existsByCorreoClienteAndContraseniaCliente(
                dto.getCorreoCliente(), dto.getContraseniaCliente()))
                .thenReturn(false);

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setIdUsuario(1L);
        clienteGuardado.setNombreCliente("Juan");
        clienteGuardado.setApellidoCliente("Pérez");
        clienteGuardado.setCorreoCliente("juan@example.com");
        clienteGuardado.setContraseniaCliente("123456");
        clienteGuardado.setDireccionEnvioCliente("Calle Falsa 123");
        clienteGuardado.setActivo(true);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        Cliente resultado = clienteService.crearCliente(dto);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombreCliente());
        assertEquals("Pérez", resultado.getApellidoCliente());
        assertEquals("juan@example.com", resultado.getCorreoCliente());

        verify(clienteRepository, times(1)).existsByCorreoClienteAndContraseniaCliente(anyString(), anyString());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Devuelve todos los clientes")
    public void debeListarTodosLosClientes(){
        when(clienteRepository.findAll()).thenReturn(this.clienteList);
        List<Cliente> resultado = clienteService.traerTodos();
        assertThat(resultado).hasSize(100);
        assertThat(resultado).contains(this.clientePrueba);

        verify(clienteRepository, times(1)).findAll();


    }

    @Test
    @DisplayName("Lanza una excepcion si no hay ningun cliente")
    public void lanzaExcepcionSiNoHayNingunCliente(){
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());
        assertThatThrownBy(()->clienteService.traerTodos())
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("No hay clientes registrados");

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
    @DisplayName("Lanza una excepcion si el id no existe")
    public void lanzarUnaExcepcionSiElIdNoExiste(){
        Long idInexistente = 999L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> clienteService.traerPorId(idInexistente))
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("Cliente con id " + idInexistente + " no encontrado");

        verify(clienteRepository, times(1)).findById(idInexistente);
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

        Cliente resultado = clienteService.actualizarCliente(1L, nuevosDatos);

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
        Long idInexistente = 999L;
        Cliente nuevosDatos = new Cliente();

        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());


        /*Aca se valida que cuando se intenta actualizar un cliente que no existe, el servicio
        * Lanza una excepcion.
        * Da un mensaje que incluye el id del cliente no encontrado.
        * */
        assertThatThrownBy(() -> clienteService.actualizarCliente(idInexistente, nuevosDatos))
            .isInstanceOf(ClienteException.class)
            .hasMessageContaining("Cliente con id 999 no encontrado");

        verify(clienteRepository, times(1)).findById(idInexistente);
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
        Long idInexistente = 999L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(idInexistente))
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("Cliente con id 999 no encontrado");

        verify(clienteRepository, never()).deleteById(idInexistente);
    }
}
