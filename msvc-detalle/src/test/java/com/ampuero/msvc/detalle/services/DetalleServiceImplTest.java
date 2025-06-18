package com.ampuero.msvc.detalle.services;

import com.ampuero.msvc.detalle.clients.BoletaClient;
import com.ampuero.msvc.detalle.clients.ProductoClient;
import com.ampuero.msvc.detalle.dtos.DetalleDTO;
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

        Exception exception = assertThrows(DetalleException.class, () -> {
            detalleService.crearDetalle(dto);
        });

        assertTrue(exception.getMessage().contains("Error al obtener la boleta"));
    }

    @Test
    void testCrearDetalle_ProductoNoExiste() {
        DetalleDTO dto = new DetalleDTO(1L, 1L, 2);

        when(boletaClient.getBoletaById(1L)).thenReturn(ResponseEntity.ok(boleta));
        when(productoClient.getProductoById(1L)).thenReturn(null); // fallo aquí

        Exception exception = assertThrows(DetalleException.class, () -> {
            detalleService.crearDetalle(dto);
        });

        assertTrue(exception.getMessage().contains("Error al obtener el producto"));
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




}
