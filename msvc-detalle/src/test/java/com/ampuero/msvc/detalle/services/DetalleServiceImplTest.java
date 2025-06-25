package com.ampuero.msvc.detalle.services;

import com.ampuero.msvc.detalle.clients.BoletaClient;
import com.ampuero.msvc.detalle.clients.ProductoClient;
import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.dtos.MontoUpdateRequestDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.models.BoletaPojo;
import com.ampuero.msvc.detalle.models.ClientePojo;
import com.ampuero.msvc.detalle.models.ProductoPojo;
import com.ampuero.msvc.detalle.models.entities.Detalle;
import com.ampuero.msvc.detalle.repositories.DetalleRepository;
import feign.FeignException;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetalleServiceImplTest {

    // Generador de datos falsos
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Mock
    private DetalleRepository detalleRepository;

    @Mock
    private BoletaClient boletaClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private DetalleServiceImpl detalleService;

    private List<Detalle> detalleList = new ArrayList<>();
    private Detalle detallePrueba;
    private BoletaPojo boletaPrueba;
    private ProductoPojo productoPrueba;
    private ClientePojo clientePrueba;
    private DetalleDTO detalleDTOPrueba;

    @BeforeEach
    void setUp() {
        // Crear cliente de prueba
        clientePrueba = new ClientePojo();
        clientePrueba.setIdUsuario(1L);
        clientePrueba.setNombreCliente(faker.name().firstName());
        clientePrueba.setCorreoCliente(faker.internet().emailAddress());

        // Crear boleta de prueba
        boletaPrueba = new BoletaPojo();
        boletaPrueba.setIdBoleta(1L);
        boletaPrueba.setFechaEmisionBoleta(LocalDate.now());
        boletaPrueba.setTotalBoleta(1000.0);
        boletaPrueba.setDescripcionBoleta("Boleta de prueba");
        boletaPrueba.setCliente(clientePrueba);

        // Crear producto de prueba
        productoPrueba = new ProductoPojo();
        productoPrueba.setIdProducto(1L);
        productoPrueba.setNombreProducto(faker.commerce().productName());
        productoPrueba.setDescripcionProducto(faker.lorem().sentence());
        productoPrueba.setPrecioProducto(100.0);

        // Crear detalle de prueba
        detallePrueba = new Detalle();
        detallePrueba.setIdDetalle(1L);
        detallePrueba.setIdBoletaPojo(1L);
        detallePrueba.setIdProductoPojo(1L);
        detallePrueba.setCantidadDetalle(2);
        detallePrueba.setPrecioUnitarioDetalle(100.0);
        detallePrueba.setSubtotalDetalle(200.0);

        // Crear DTO de prueba
        detalleDTOPrueba = new DetalleDTO();
        detalleDTOPrueba.setIdBoletaPojo(1L);
        detalleDTOPrueba.setIdProductoPojo(1L);
        detalleDTOPrueba.setCantidadDetalle(2);

        // Poblar lista de detalles para tests
        for (int i = 0; i < 10; i++) {
            Detalle detalle = new Detalle();
            detalle.setIdDetalle((long) i + 1);
            detalle.setIdBoletaPojo((long) ((i % 3) + 1)); // 3 boletas diferentes
            detalle.setIdProductoPojo((long) ((i % 5) + 1)); // 5 productos diferentes
            detalle.setCantidadDetalle(faker.number().numberBetween(1, 10));
            detalle.setPrecioUnitarioDetalle(faker.number().randomDouble(2, 50, 500));
            detalle.setSubtotalDetalle(detalle.getCantidadDetalle() * detalle.getPrecioUnitarioDetalle());
            detalleList.add(detalle);
        }
    }

    // ================ TESTS PARA CREAR DETALLE ================

    @Test
    @DisplayName("Debe crear detalle exitosamente cuando boleta y producto existen")
    void debeCrearDetalleExitosamente() throws Exception {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detallePrueba);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(detalleDTOPrueba);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCantidadDetalle()).isEqualTo(2);
        assertThat(resultado.getPrecioUnitarioDetalle()).isEqualTo(100.0);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(200.0);

        verify(boletaClient, times(1)).getBoletaById(1L);
        verify(productoClient, times(1)).getProductoById(1L);
        verify(detalleRepository, times(1)).save(any(Detalle.class));
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando boleta no existe")
    void debeLanzarExcepcionCuandoBoletaNoExiste() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.notFound().build());

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error del servicio de boletas");

        verify(productoClient, never()).getProductoById(anyLong());
        verify(detalleRepository, never()).save(any(Detalle.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando producto no existe")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.notFound().build());

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error del servicio de productos");

        verify(detalleRepository, never()).save(any(Detalle.class));
    }

    @Test
    @DisplayName("Debe crear detalle con cantidad cero")
    void debeCrearDetalleConCantidadCero() throws Exception {
        // Given
        detalleDTOPrueba.setCantidadDetalle(0);
        Detalle detalleConCantidadCero = new Detalle();
        detalleConCantidadCero.setIdDetalle(1L);
        detalleConCantidadCero.setIdBoletaPojo(1L);
        detalleConCantidadCero.setIdProductoPojo(1L);
        detalleConCantidadCero.setCantidadDetalle(0);
        detalleConCantidadCero.setPrecioUnitarioDetalle(100.0);
        detalleConCantidadCero.setSubtotalDetalle(0.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleConCantidadCero);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(detalleDTOPrueba);

        // Then
        assertThat(resultado.getCantidadDetalle()).isEqualTo(0);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debe manejar error de comunicación con servicio de boletas")
    void debeManejarnErrorComunicacionBoletas() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenThrow(new RuntimeException("Servicio no disponible"));

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no disponible");
    }

    // ================ TESTS PARA OBTENER TODOS ================

    @Test
    @DisplayName("Debe obtener todos los detalles exitosamente")
    void debeObtenerTodosLosDetalles() {
        // Given
        when(detalleRepository.findAll()).thenReturn(detalleList);

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerTodos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(10);
        verify(detalleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay detalles")
    void debeRetornarListaVaciaCuandoNoHayDetalles() {
        // Given
        when(detalleRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerTodos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }

    // ================ TESTS PARA OBTENER POR BOLETA ================

    @Test
    @DisplayName("Debe obtener detalles por boleta exitosamente")
    void debeObtenerDetallesPorBoleta() {
        // Given
        List<Detalle> detallesBoleta = detalleList.stream()
                .filter(d -> d.getIdBoletaPojo().equals(1L))
                .toList();
        when(detalleRepository.findByIdBoletaPojo(1L)).thenReturn(detallesBoleta);

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerPorBoleta(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allMatch(dto -> dto.getBoleta().getIdBoleta().equals(1L));
    }

    @Test
    @DisplayName("Debe retornar lista vacía para boleta sin detalles")
    void debeRetornarListaVaciaParaBoletaSinDetalles() {
        // Given
        when(detalleRepository.findByIdBoletaPojo(999L)).thenReturn(new ArrayList<>());

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerPorBoleta(999L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }

    // ================ TESTS PARA OBTENER POR ID ================

    @Test
    @DisplayName("Debe obtener detalle por ID exitosamente")
    void debeObtenerDetallePorId() throws Exception {
        // Given
        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));

        // When
        DetalleResponseDTO resultado = detalleService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdDetalle()).isEqualTo(1L);
        assertThat(resultado.getCantidadDetalle()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando detalle no existe por ID")
    void debeLanzarExcepcionCuandoDetalleNoExistePorId() {
        // Given
        when(detalleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> detalleService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Detalle no encontrado con ID: 999");

        verify(boletaClient, never()).getBoletaById(anyLong());
        verify(productoClient, never()).getProductoById(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando falla obtener dependencias")
    void debeLanzarExcepcionCuandoFallaObtenerDependencias() {
        // Given
        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        when(boletaClient.getBoletaById(1L)).thenThrow(new RuntimeException("Servicio no disponible"));

        // When & Then
        assertThatThrownBy(() -> detalleService.obtenerPorId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no disponible");
    }

    // ================ TESTS PARA ACTUALIZAR DETALLE ================

    @Test
    @DisplayName("Debe actualizar detalle exitosamente con misma boleta")
    void debeActualizarDetalleConMismaBoleta() throws Exception {
        // Given
        DetalleDTO nuevosDatos = new DetalleDTO();
        nuevosDatos.setIdBoletaPojo(1L); // Misma boleta
        nuevosDatos.setIdProductoPojo(1L);
        nuevosDatos.setCantidadDetalle(5); // Nueva cantidad

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));
        when(detalleRepository.save(any(Detalle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.actualizarDetalle(1L, nuevosDatos);

        // Then
        assertThat(resultado.getCantidadDetalle()).isEqualTo(5);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(500.0); // 5 * 100

        // Verificar que solo se actualizó una vez la boleta (diferencia)
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));
    }

    @Test
    @DisplayName("Debe actualizar detalle cambiando de boleta")
    void debeActualizarDetalleCambiandoBoleta() throws Exception {
        // Given
        BoletaPojo nuevaBoleta = new BoletaPojo();
        nuevaBoleta.setIdBoleta(2L);
        nuevaBoleta.setFechaEmisionBoleta(LocalDate.now());
        nuevaBoleta.setTotalBoleta(500.0);
        nuevaBoleta.setDescripcionBoleta("Nueva boleta");
        nuevaBoleta.setCliente(clientePrueba);

        DetalleDTO nuevosDatos = new DetalleDTO();
        nuevosDatos.setIdBoletaPojo(2L); // Cambio de boleta
        nuevosDatos.setIdProductoPojo(1L);
        nuevosDatos.setCantidadDetalle(3);

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        when(boletaClient.getBoletaById(2L)).thenReturn(ResponseEntity.ok(nuevaBoleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));
        when(detalleRepository.save(any(Detalle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(boletaClient).actualizarTotalBoleta(anyLong(), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.actualizarDetalle(1L, nuevosDatos);

        // Then
        assertThat(resultado.getCantidadDetalle()).isEqualTo(3);

        // Verificar que se actualizaron ambas boletas
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class)); // Boleta original
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(2L), any(MontoUpdateRequestDTO.class)); // Nueva boleta
    }

    @Test
    @DisplayName("Debe lanzar DetalleException cuando detalle no existe para actualizar")
    void debeLanzarExcepcionCuandoDetalleNoExisteParaActualizar() {
        // Given
        when(detalleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> detalleService.actualizarDetalle(999L, detalleDTOPrueba))
                .isInstanceOf(DetalleException.class)
                .hasMessageContaining("Detalle no encontrado con ID: 999");

        verify(boletaClient, never()).getBoletaById(anyLong());
        verify(productoClient, never()).getProductoById(anyLong());
        verify(detalleRepository, never()).save(any(Detalle.class));
    }

    @Test
    @DisplayName("Debe actualizar detalle con producto diferente")
    void debeActualizarDetalleConProductoDiferente() throws Exception {
        // Given
        ProductoPojo nuevoProducto = new ProductoPojo();
        nuevoProducto.setIdProducto(2L);
        nuevoProducto.setNombreProducto("Nuevo Producto");
        nuevoProducto.setPrecioProducto(150.0);

        DetalleDTO nuevosDatos = new DetalleDTO();
        nuevosDatos.setIdBoletaPojo(1L);
        nuevosDatos.setIdProductoPojo(2L); // Cambio de producto
        nuevosDatos.setCantidadDetalle(3);

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(2L)).thenReturn(ResponseEntity.ok(nuevoProducto));
        when(detalleRepository.save(any(Detalle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.actualizarDetalle(1L, nuevosDatos);

        // Then
        assertThat(resultado.getCantidadDetalle()).isEqualTo(3);
        assertThat(resultado.getPrecioUnitarioDetalle()).isEqualTo(150.0);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(450.0); // 3 * 150
    }

    // ================ TESTS PARA ELIMINAR DETALLE ================

    @Test
    @DisplayName("Debe eliminar detalle exitosamente")
    void debeEliminarDetalleExitosamente() throws Exception {
        // Given
        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detallePrueba));
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));
        doNothing().when(detalleRepository).delete(detallePrueba);

        // When
        detalleService.eliminarDetalle(1L);

        // Then
        verify(detalleRepository, times(1)).findById(1L);
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));
        verify(detalleRepository, times(1)).delete(detallePrueba);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando detalle no existe para eliminar")
    void debeLanzarExcepcionCuandoDetalleNoExisteParaEliminar() {
        // Given
        when(detalleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> detalleService.eliminarDetalle(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Detalle no encontrado con ID: 999");

        verify(boletaClient, never()).actualizarTotalBoleta(anyLong(), any(MontoUpdateRequestDTO.class));
        verify(detalleRepository, never()).delete(any(Detalle.class));
    }

    // ================ TESTS DE CASOS LÍMITE Y EXCEPCIONES ================

    @Test
    @DisplayName("Debe manejar producto con precio negativo")
    void debeManejarnProductoConPrecioNegativo() throws Exception {
        // Given
        ProductoPojo productoConPrecioNegativo = new ProductoPojo();
        productoConPrecioNegativo.setIdProducto(1L);
        productoConPrecioNegativo.setPrecioProducto(-50.0);

        Detalle detalleConPrecioNegativo = new Detalle();
        detalleConPrecioNegativo.setIdDetalle(1L);
        detalleConPrecioNegativo.setIdBoletaPojo(1L);
        detalleConPrecioNegativo.setIdProductoPojo(1L);
        detalleConPrecioNegativo.setCantidadDetalle(2);
        detalleConPrecioNegativo.setPrecioUnitarioDetalle(-50.0);
        detalleConPrecioNegativo.setSubtotalDetalle(-100.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoConPrecioNegativo));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleConPrecioNegativo);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(detalleDTOPrueba);

        // Then
        assertThat(resultado.getPrecioUnitarioDetalle()).isEqualTo(-50.0);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(-100.0);
    }

    @Test
    @DisplayName("Debe manejar respuesta nula del servicio de boletas")
    void debeManejarnRespuestaNulaDelServicioBoletas() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(null));

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Respuesta nula o boleta sin ID");
    }

    @Test
    @DisplayName("Debe manejar respuesta nula del servicio de productos")
    void debeManejarnRespuestaNulaDelServicioProductos() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(null));

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Respuesta nula o producto sin ID");
    }

    @Test
    @DisplayName("Debe manejar FeignException con status 404 para boletas")
    void debeManejarnFeignExceptionStatus404Boletas() {
        // Given
        FeignException feignException = FeignException.errorStatus("getBoletaById", 
                feign.Response.builder()
                        .status(404)
                        .reason("Not Found")
                        .request(feign.Request.create(feign.Request.HttpMethod.GET, "test", new java.util.HashMap<>(), null, null, null))
                        .headers(new java.util.HashMap<>())
                        .body(new byte[0])
                        .build());
        when(boletaClient.getBoletaById(1L)).thenThrow(feignException);

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Boleta no encontrada con ID: 1");
    }

    @Test
    @DisplayName("Debe manejar FeignException con status 404 para productos")
    void debeManejarnFeignExceptionStatus404Productos() {
        // Given
        FeignException feignException = FeignException.errorStatus("getProductoById", 
                feign.Response.builder()
                        .status(404)
                        .reason("Not Found")
                        .request(feign.Request.create(feign.Request.HttpMethod.GET, "test", new java.util.HashMap<>(), null, null, null))
                        .headers(new java.util.HashMap<>())
                        .body(new byte[0])
                        .build());
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenThrow(feignException);

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto no encontrado con ID: 1");
    }

    @Test
    @DisplayName("Debe manejar error interno del servidor en servicio de boletas")
    void debeManejarnErrorInternoServidorBoletas() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error del servicio de boletas");
    }

    @Test
    @DisplayName("Debe manejar error interno del servidor en servicio de productos")
    void debeManejarnErrorInternoServidorProductos() {
        // Given
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error del servicio de productos");
    }

    @Test
    @DisplayName("Debe crear detalle con cantidad muy alta")
    void debeCrearDetalleConCantidadMuyAlta() throws Exception {
        // Given
        detalleDTOPrueba.setCantidadDetalle(1000);
        Detalle detalleConCantidadAlta = new Detalle();
        detalleConCantidadAlta.setIdDetalle(1L);
        detalleConCantidadAlta.setIdBoletaPojo(1L);
        detalleConCantidadAlta.setIdProductoPojo(1L);
        detalleConCantidadAlta.setCantidadDetalle(1000);
        detalleConCantidadAlta.setPrecioUnitarioDetalle(100.0);
        detalleConCantidadAlta.setSubtotalDetalle(100000.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoPrueba));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleConCantidadAlta);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(detalleDTOPrueba);

        // Then
        assertThat(resultado.getCantidadDetalle()).isEqualTo(1000);
        assertThat(resultado.getSubtotalDetalle()).isEqualTo(100000.0);
    }

    @Test
    @DisplayName("Debe manejar boleta con ID nulo")
    void debeManejarnBoletaConIdNulo() {
        // Given
        BoletaPojo boletaSinId = new BoletaPojo();
        boletaSinId.setIdBoleta(null); // ID nulo
        boletaSinId.setDescripcionBoleta("Boleta sin ID");

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaSinId));

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Respuesta nula o boleta sin ID");
    }

    @Test
    @DisplayName("Debe manejar producto con ID nulo")
    void debeManejarnProductoConIdNulo() {
        // Given
        ProductoPojo productoSinId = new ProductoPojo();
        productoSinId.setIdProducto(null); // ID nulo
        productoSinId.setNombreProducto("Producto sin ID");

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boletaPrueba));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoSinId));

        // When & Then
        assertThatThrownBy(() -> detalleService.crearDetalle(detalleDTOPrueba))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Respuesta nula o producto sin ID");
    }
}