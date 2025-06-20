package com.ampuero.msvc.producto.services;

import com.ampuero.msvc.producto.exceptions.ProductoException;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.repositories.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoServiceImpl.
 * 
 * Estas pruebas cubren todos los métodos del servicio con casos exitosos y fallidos:
 * - Crear producto (exitoso)
 * - Obtener todos los productos (exitoso y vacío)
 * - Obtener producto por ID (exitoso y no encontrado)
 * - Actualizar producto (exitoso y no encontrado)
 * - Eliminar producto (exitoso y no encontrado)
 */
@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Mock
    private ProductoRepository productoRepository;

    private Producto producto;
    private Producto productoActualizado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Producto de prueba
        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombreProducto("Laptop Gaming");
        producto.setDescripcionProducto("Laptop para gaming de alta gama");
        producto.setPrecioProducto(1500.00);

        // Producto actualizado para pruebas
        productoActualizado = new Producto();
        productoActualizado.setIdProducto(1L);
        productoActualizado.setNombreProducto("Laptop Gaming Pro");
        productoActualizado.setDescripcionProducto("Laptop para gaming profesional");
        productoActualizado.setPrecioProducto(1800.00);
    }

    // ========== PRUEBAS PARA CREAR PRODUCTO ==========

    @Test
    void testCrearProducto_Exitoso() {
        // Given
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombreProducto("Mouse Gaming");
        nuevoProducto.setDescripcionProducto("Mouse para gaming RGB");
        nuevoProducto.setPrecioProducto(50.00);

        Producto productoGuardado = new Producto();
        productoGuardado.setIdProducto(2L);
        productoGuardado.setNombreProducto("Mouse Gaming");
        productoGuardado.setDescripcionProducto("Mouse para gaming RGB");
        productoGuardado.setPrecioProducto(50.00);

        when(productoRepository.save(nuevoProducto)).thenReturn(productoGuardado);

        // When
        Producto resultado = productoService.crearProducto(nuevoProducto);

        // Then
        assertNotNull(resultado);
        assertEquals(2L, resultado.getIdProducto());
        assertEquals("Mouse Gaming", resultado.getNombreProducto());
        assertEquals("Mouse para gaming RGB", resultado.getDescripcionProducto());
        assertEquals(50.00, resultado.getPrecioProducto());
        
        verify(productoRepository, times(1)).save(nuevoProducto);
    }

    // ========== PRUEBAS PARA OBTENER TODOS LOS PRODUCTOS ==========

    @Test
    void testTraerTodo_ConProductos() {
        // Given
        Producto producto2 = new Producto();
        producto2.setIdProducto(2L);
        producto2.setNombreProducto("Teclado Mecánico");
        producto2.setDescripcionProducto("Teclado mecánico RGB");
        producto2.setPrecioProducto(120.00);

        List<Producto> productos = Arrays.asList(producto, producto2);
        when(productoRepository.findAll()).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop Gaming", resultado.get(0).getNombreProducto());
        assertEquals("Teclado Mecánico", resultado.get(1).getNombreProducto());
        
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testTraerTodo_ListaVacia() {
        // Given
        when(productoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        
        verify(productoRepository, times(1)).findAll();
    }

    // ========== PRUEBAS PARA OBTENER PRODUCTO POR ID ==========

    @Test
    void testTraerPorId_ProductoExistente() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Producto resultado = productoService.traerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Laptop Gaming", resultado.getNombreProducto());
        assertEquals("Laptop para gaming de alta gama", resultado.getDescripcionProducto());
        assertEquals(1500.00, resultado.getPrecioProducto());
        
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testTraerPorId_ProductoNoExistente() {
        // Given
        Long idInexistente = 999L;
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then
        ProductoException exception = assertThrows(ProductoException.class, () -> {
            productoService.traerPorId(idInexistente);
        });

        assertTrue(exception.getMessage().contains("El producto con el id" + idInexistente + "no existe"));
        verify(productoRepository, times(1)).findById(idInexistente);
    }

    // ========== PRUEBAS PARA ACTUALIZAR PRODUCTO ==========

    @Test
    void testActualizarProducto_Exitoso() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Laptop Gaming Pro");
        datosActualizacion.setDescripcionProducto("Laptop para gaming profesional");
        datosActualizacion.setPrecioProducto(1800.00);

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Laptop Gaming Pro", resultado.getNombreProducto());
        assertEquals("Laptop para gaming profesional", resultado.getDescripcionProducto());
        assertEquals(1800.00, resultado.getPrecioProducto());
        
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarProducto_ProductoNoExistente() {
        // Given
        Long idInexistente = 999L;
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Producto Actualizado");

        // When & Then
        ProductoException exception = assertThrows(ProductoException.class, () -> {
            productoService.actualizarProducto(idInexistente, datosActualizacion);
        });

        assertTrue(exception.getMessage().contains("El medico on el id" + idInexistente + "no existe"));
        verify(productoRepository, times(1)).findById(idInexistente);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // ========== PRUEBAS PARA ELIMINAR PRODUCTO ==========

    @Test
    void testEliminarProducto_ProductoExistente() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        // When
        productoService.eliminarProducto(1L);

        // Then
        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarProducto_ProductoNoExistente() {
        // Given
        Long idInexistente = 999L;
        when(productoRepository.existsById(idInexistente)).thenReturn(false);

        // When
        productoService.eliminarProducto(idInexistente);

        // Then
        verify(productoRepository, times(1)).existsById(idInexistente);
        verify(productoRepository, never()).deleteById(idInexistente);
    }

    // ========== PRUEBAS ADICIONALES PARA CASOS ESPECIALES ==========

    @Test
    void testCrearProducto_ConDatosNulos() {
        // Given
        Producto productoConNulos = new Producto();
        productoConNulos.setNombreProducto("Producto Test");
        // descripcion y precio quedan null/0

        when(productoRepository.save(productoConNulos)).thenReturn(productoConNulos);

        // When
        Producto resultado = productoService.crearProducto(productoConNulos);

        // Then
        assertNotNull(resultado);
        assertEquals("Producto Test", resultado.getNombreProducto());
        verify(productoRepository, times(1)).save(productoConNulos);
    }

    @Test
    void testActualizarProducto_ActualizacionParcial() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        // Simulamos la actualización donde solo se cambia el nombre
        Producto productoConSoloNombre = new Producto();
        productoConSoloNombre.setIdProducto(1L);
        productoConSoloNombre.setNombreProducto("Laptop Gaming Actualizada");
        productoConSoloNombre.setDescripcionProducto(null); // Se actualizará a null
        productoConSoloNombre.setPrecioProducto(0.0); // Se actualizará a 0

        when(productoRepository.save(any(Producto.class))).thenReturn(productoConSoloNombre);

        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Laptop Gaming Actualizada");

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertNotNull(resultado);
        assertEquals("Laptop Gaming Actualizada", resultado.getNombreProducto());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
} 