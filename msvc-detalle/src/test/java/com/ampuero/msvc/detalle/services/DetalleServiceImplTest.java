package com.ampuero.msvc.detalle.services;

import com.ampuero.msvc.detalle.clients.BoletaClient;
import com.ampuero.msvc.detalle.clients.ProductoClient;
import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.dtos.MontoUpdateRequestDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.models.BoletaPojo;
import com.ampuero.msvc.detalle.models.ProductoPojo;
import com.ampuero.msvc.detalle.models.entities.Detalle;
import com.ampuero.msvc.detalle.repositories.DetalleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetalleServiceImplTest {

    @InjectMocks
    private DetalleServiceImpl detalleService;

    @Mock
    private DetalleRepository detalleRepository;

    @Mock
    private BoletaClient boletaClient;

    @Mock
    private ProductoClient productoClient;

    private BoletaPojo boleta;
    private ProductoPojo producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detalleService = new DetalleServiceImpl(detalleRepository, productoClient,boletaClient);

        boleta = new BoletaPojo();
        boleta.setIdBoleta(1L);

        producto = new ProductoPojo();
        producto.setIdProducto(1L);
        producto.setPrecioProducto(100.0);
    }

    @Test
    void testCrearDetalle_Exito() throws ResourceNotFoundException, DetalleException {
        DetalleDTO dto = new DetalleDTO(1L, 1L, 2);
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalle);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        var response = detalleService.crearDetalle(dto);

        assertNotNull(response);
        assertEquals(2, response.getCantidadDetalle());
        assertEquals(100.0, response.getPrecioUnitarioDetalle());
        assertEquals(200.0, response.getSubtotalDetalle());
    }

    @Test
    void testObtenerTodos_ConDetallesValidos() throws ResourceNotFoundException {
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        when(detalleRepository.findAll()).thenReturn(List.of(detalle));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));

        var result = detalleService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getSubtotalDetalle());
    }

    @Test
    void testActualizarDetalle_CambioCantidad() throws Exception {
        Detalle detalleExistente = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        DetalleDTO dto = new DetalleDTO(1L, 1L, 3); // cambio de cantidad

        producto.setPrecioProducto(120.0); // nuevo precio

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalleExistente));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleExistente);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any(MontoUpdateRequestDTO.class));

        var actualizado = detalleService.actualizarDetalle(1L, dto);

        assertEquals(3, actualizado.getCantidadDetalle());
        assertEquals(120.0, actualizado.getPrecioUnitarioDetalle());
        assertEquals(360.0, actualizado.getSubtotalDetalle());
    }

    @Test
    void testEliminarDetalle_Exito() throws Exception {
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalle));
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any());
        doNothing().when(detalleRepository).delete(detalle);

        detalleService.eliminarDetalle(1L);

        verify(detalleRepository, times(1)).delete(detalle);
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any());
    }

    @Test
    void testCrearDetalle_BoletaNoExiste() {
        DetalleDTO dto = new DetalleDTO(1L, 1L, 2);
        when(boletaClient.getBoletaById(1L))
                .thenReturn(ResponseEntity.notFound().build());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            detalleService.crearDetalle(dto);
        });

        assertFalse(exception.getMessage().contains("Error al obtener la boleta"));
    }

    @Test
    void testCrearDetalle_ProductoNoExiste() {
        DetalleDTO dto = new DetalleDTO(1L, 1L, 2);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(null); // fallo aquí

        Exception exception = assertThrows(NullPointerException.class, () -> {
            detalleService.crearDetalle(dto);
        });

        assertFalse(exception.getMessage().contains("Error al obtener el producto"));
    }

    @Test
    void testObtenerTodos_BoletaNoEncontrada() {
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        when(detalleRepository.findAll()).thenReturn(List.of(detalle));
        when(boletaClient.getBoletaById(1L)).thenReturn(null); // error

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            detalleService.obtenerTodos();
        });

        assertTrue(exception.getMessage().contains("Boleta no encontrada con ID"));
    }

    @Test
    void testActualizarDetalle_DetalleNoExiste() {
        DetalleDTO dto = new DetalleDTO(1L, 1L, 3);

        when(detalleRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DetalleException.class, () -> {
            detalleService.actualizarDetalle(1L, dto);
        });

        assertTrue(exception.getMessage().contains("Detalle no encontrado con ID"));
    }

    @Test
    void testEliminarDetalle_DetalleNoExiste() {
        when(detalleRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            detalleService.eliminarDetalle(1L);
        });

        assertTrue(exception.getMessage().contains("Detalle no encontrado con ID"));
    }

    // ========== PRUEBAS ADICIONALES PARA CASOS COMPLEJOS ==========

    @Test
    void testObtenerPorBoleta_ConDetallesValidos() {
        // Given
        Detalle detalle1 = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        Detalle detalle2 = new Detalle(2L, 1L, 2L, 1, 150.0, 150.0);
        
        ProductoPojo producto2 = new ProductoPojo();
        producto2.setIdProducto(2L);
        producto2.setPrecioProducto(150.0);

        when(detalleRepository.findByIdBoletaPojo(1L)).thenReturn(List.of(detalle1, detalle2));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(productoClient.getProductoById(2L)).thenReturn(ResponseEntity.ok(producto2));

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerPorBoleta(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(200.0, resultado.get(0).getSubtotalDetalle());
        assertEquals(150.0, resultado.get(1).getSubtotalDetalle());
    }

    @Test
    void testObtenerPorBoleta_ConDetallesSinProducto() {
        // Given
        Detalle detalle = new Detalle(1L, 1L, 999L, 2, 100.0, 200.0); // Producto inexistente
        
        when(detalleRepository.findByIdBoletaPojo(1L)).thenReturn(List.of(detalle));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(999L)).thenReturn(null);

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerPorBoleta(1L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty()); // El detalle se omite por producto inexistente
    }

    @Test
    void testCrearDetalle_ConCantidadCero() throws Exception {
        // Given
        DetalleDTO dto = new DetalleDTO(1L, 1L, 0); // Cantidad cero
        Detalle detalle = new Detalle(1L, 1L, 1L, 0, 100.0, 0.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalle);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any());

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(0, resultado.getCantidadDetalle());
        assertEquals(0.0, resultado.getSubtotalDetalle());
    }

    @Test
    void testActualizarDetalle_CambioDeBoleta() throws Exception {
        // Given
        Detalle detalleExistente = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        DetalleDTO dto = new DetalleDTO(2L, 1L, 3); // Cambio de boleta
        
        BoletaPojo nuevaBoleta = new BoletaPojo();
        nuevaBoleta.setIdBoleta(2L);

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalleExistente));
        when(boletaClient.getBoletaById(2L)).thenReturn(ResponseEntity.ok(nuevaBoleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleExistente);
        doNothing().when(boletaClient).actualizarTotalBoleta(anyLong(), any());

        // When
        DetalleResponseDTO resultado = detalleService.actualizarDetalle(1L, dto);

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.getCantidadDetalle());
        
        // Verificar que se actualizaron ambas boletas
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any()); // Boleta original
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(2L), any()); // Nueva boleta
    }

    @Test
    void testObtenerTodos_ConFalloEnComunicacion() {
        // Given
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        when(detalleRepository.findAll()).thenReturn(List.of(detalle));
        when(boletaClient.getBoletaById(1L)).thenThrow(new RuntimeException("Servicio no disponible"));

        // When
        List<DetalleResponseDTO> resultado = detalleService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty()); // Se omiten los detalles con errores
    }

    @Test
    void testCrearDetalle_ConPrecioProductoNegativo() throws Exception {
        // Given
        DetalleDTO dto = new DetalleDTO(1L, 1L, 2);
        
        ProductoPojo productoConPrecioNegativo = new ProductoPojo();
        productoConPrecioNegativo.setIdProducto(1L);
        productoConPrecioNegativo.setPrecioProducto(-50.0); // Precio negativo
        
        Detalle detalle = new Detalle(1L, 1L, 1L, 2, -50.0, -100.0);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(productoConPrecioNegativo));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalle);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any());

        // When
        DetalleResponseDTO resultado = detalleService.crearDetalle(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(-50.0, resultado.getPrecioUnitarioDetalle());
        assertEquals(-100.0, resultado.getSubtotalDetalle());
    }

    @Test
    void testActualizarDetalle_MismaBoleta() throws Exception {
        // Given
        Detalle detalleExistente = new Detalle(1L, 1L, 1L, 2, 100.0, 200.0);
        DetalleDTO dto = new DetalleDTO(1L, 1L, 5); // Misma boleta, nueva cantidad

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalleExistente));
        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(ResponseEntity.ok(producto));
        when(detalleRepository.save(any(Detalle.class))).thenReturn(detalleExistente);
        doNothing().when(boletaClient).actualizarTotalBoleta(eq(1L), any());

        // When
        DetalleResponseDTO resultado = detalleService.actualizarDetalle(1L, dto);

        // Then
        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidadDetalle());
        
        // Verificar que solo se actualizó una vez la boleta (diferencia)
        verify(boletaClient, times(1)).actualizarTotalBoleta(eq(1L), any());
    }

}
