package com.ampuero.msvc.boleta.services;

import com.ampuero.msvc.boleta.clients.ClienteClientRest;
import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import com.ampuero.msvc.boleta.dtos.ClienteResponseDTO;
import com.ampuero.msvc.boleta.exceptions.BoletaException;
import com.ampuero.msvc.boleta.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.boleta.models.entities.Boleta;
import com.ampuero.msvc.boleta.repositories.BoletaRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


// Habilitamos la integración de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
public class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ClienteClientRest clienteClientRest;

    @InjectMocks
    private BoletaServiceImpl boletaService;

    private List<Boleta> boletaList = new ArrayList<>();

    private Boleta boletaPrueba;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));
        for(int i=0;i<100;i++){
            Boleta boleta = new Boleta();
            boleta.setIdBoleta((long) i+1);
            boleta.setFechaEmisionBoleta(LocalDate.now().minusDays(faker.number().numberBetween(0, 90)));
            boleta.setTotalBoleta(faker.number().randomDouble(2, 1000, 50000));
            boleta.setDescripcionBoleta("Boleta por compra de " + faker.commerce().productName());
            boleta.setIdClientePojo((long) faker.number().numberBetween(1, 50));
            this.boletaList.add(boleta);
        }
        this.boletaPrueba = this.boletaList.get(0);
    }

    @Test
    @DisplayName("Debe crear una boleta correctamente")
    public void crearBoletaCuandoClienteExiste() {
        BoletaDTO dto = new BoletaDTO("Compra test", 1L);
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setIdUsuario(1L);

        when(clienteClientRest.findClienteById(1L)).thenReturn(cliente);
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(i -> {
            Boleta b = i.getArgument(0);
            b.setIdBoleta(100L);
            return b;
        });

        BoletaResponseDTO resultado = boletaService.crearBoleta(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdBoleta()).isEqualTo(100L);
        assertThat(resultado.getCliente().getIdUsuario()).isEqualTo(1L);

        verify(clienteClientRest, times(1)).findClienteById(1L);
        verify(boletaRepository, times(1)).save(any(Boleta.class));
    }

    @Test
    @DisplayName("Debe obtener boleta por id")
    public void DebeObtenerBoletaPorIdCuandoExiste() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaPrueba));
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setIdUsuario(1L);
        when(clienteClientRest.findClienteById(anyLong())).thenReturn(cliente);

        BoletaResponseDTO resultado = boletaService.obtenerBoletaPorId(1L);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el id no existe")
    public void DebeLanzarExcepcionSiElIdNoExiste() {
        Long idInexistente = 99L;
        when(boletaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletaService.obtenerBoletaPorId(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Boleta no encontrada con ID: " + idInexistente);
    }

    @Test
    @DisplayName("Obtener todas las boletas con sus Clientes")
    public void obtenerTodasLasBoletasOmiteBoletasConClienteInvalido() {
        when(boletaRepository.findAll()).thenReturn(boletaList);
        when(clienteClientRest.findClienteById(anyLong())).thenReturn(new ClienteResponseDTO());

        List<BoletaResponseDTO> resultado = boletaService.obtenerTodas();

        assertThat(resultado).hasSize(100);

        verify(boletaRepository, times(1)).findAll();
        verify(clienteClientRest, times(100)).findClienteById(anyLong());
    }

    @Test
    @DisplayName("Retorna lista vacia si no hay boletas registradas")
    public void debeRetornarListaVaciaSiNoHayBoletas() {
        when(boletaRepository.findAll()).thenReturn(new ArrayList<>());

        List<BoletaResponseDTO> resultado = boletaService.obtenerTodas();

        assertThat(resultado).isEmpty();
        verify(boletaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar el total de Boleta por ID cuando existe.")
    public void debeActualizarTotalBoletaPorIdCuandoExiste() {
        Boleta boleta = new Boleta();
        boleta.setIdBoleta(1L);
        boleta.setTotalBoleta(100.0);

        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boleta));

        boletaService.actualizarTotalBoleta(1L, 50.0);

        verify(boletaRepository, times(1)).findById(1L);
        verify(boletaRepository).save(argThat(b -> b.getTotalBoleta() == 150.0));
    }

    @Test
    @DisplayName("Lanza excepcion si la boleta no existe al intentar actualizar el total")
    public void lanzaExcepcionAlActualizarBoletaNoExiste() {
        Long idInexistente = 99L;
        when(boletaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletaService.actualizarTotalBoleta(idInexistente, 50.0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Boleta no encontrada con ID: "+ idInexistente + " para actualizar total");
        verify(boletaRepository, times(1)).findById(idInexistente);
        verify(boletaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar Boleta por ID cuando existe")
    public void debeEliminarBoletaPorIdCuandoExiste() {


        when(boletaRepository.existsById(1L)).thenReturn(true);

        boletaService.eliminarBoleta(1L);

        verify(boletaRepository, times(1)).existsById(1L);
        verify(boletaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Lanza una excepcion si boleta no existe al eliminar")
    public void lanzaExcepcionSiBoletaNoExisteAlEliminar() {
        Long idInexistente = 99L;
        when(boletaRepository.existsById(idInexistente)).thenReturn(false);
        assertThatThrownBy(() -> boletaService.eliminarBoleta(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Boleta con ID " + idInexistente + " no encontrada para eliminar");

        verify(boletaRepository, times(1)).existsById(idInexistente);
        verify(boletaRepository, never()).deleteById(idInexistente);
    }

}
