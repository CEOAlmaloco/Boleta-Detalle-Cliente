package com.ampuero.msvc.producto.services;

import com.ampuero.msvc.producto.exceptions.ProductoException;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.repositories.ProductoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    // Generador de datos falsos
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private List<Producto> productoList = new ArrayList<>();
    private Producto productoPrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear producto de prueba
        productoPrueba = new Producto();
        productoPrueba.setIdProducto(1L);
        productoPrueba.setNombreProducto(faker.commerce().productName());
        productoPrueba.setDescripcionProducto(faker.lorem().sentence());
        productoPrueba.setPrecioProducto(faker.number().randomDouble(2, 10, 1000));

        // Poblar lista de productos para tests
        for (int i = 0; i < 15; i++) {
            Producto producto = new Producto();
            producto.setIdProducto((long) i + 1);
            producto.setNombreProducto(faker.commerce().productName());
            producto.setDescripcionProducto(faker.lorem().sentence());
            producto.setPrecioProducto(faker.number().randomDouble(2, 10, 2000));
            productoList.add(producto);
        }
    }

    // ================ TESTS PARA CREAR PRODUCTO ================

    @Test
    @DisplayName("Debe crear producto exitosamente con datos válidos")
    void debeCrearProductoExitosamente() {
        // Given
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombreProducto("Laptop Gaming");
        nuevoProducto.setDescripcionProducto("Laptop para gaming de alta gama");
        nuevoProducto.setPrecioProducto(1500.00);

        Producto productoGuardado = new Producto();
        productoGuardado.setIdProducto(1L);
        productoGuardado.setNombreProducto("Laptop Gaming");
        productoGuardado.setDescripcionProducto("Laptop para gaming de alta gama");
        productoGuardado.setPrecioProducto(1500.00);

        when(productoRepository.save(nuevoProducto)).thenReturn(productoGuardado);

        // When
        Producto resultado = productoService.crearProducto(nuevoProducto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        assertThat(resultado.getNombreProducto()).isEqualTo("Laptop Gaming");
        assertThat(resultado.getDescripcionProducto()).isEqualTo("Laptop para gaming de alta gama");
        assertThat(resultado.getPrecioProducto()).isEqualTo(1500.00);

        verify(productoRepository, times(1)).save(nuevoProducto);
    }

    @Test
    @DisplayName("Debe crear producto con precio mínimo")
    void debeCrearProductoConPrecioMinimo() {
        // Given
        Producto productoBarato = new Producto();
        productoBarato.setNombreProducto("Borrador");
        productoBarato.setDescripcionProducto("Borrador básico");
        productoBarato.setPrecioProducto(0.50);

        when(productoRepository.save(productoBarato)).thenReturn(productoBarato);

        // When
        Producto resultado = productoService.crearProducto(productoBarato);

        // Then
        assertThat(resultado.getPrecioProducto()).isEqualTo(0.50);
        verify(productoRepository, times(1)).save(productoBarato);
    }

    @Test
    @DisplayName("Debe crear producto con precio muy alto")
    void debeCrearProductoConPrecioMuyAlto() {
        // Given
        Producto productoCaro = new Producto();
        productoCaro.setNombreProducto("Servidor Enterprise");
        productoCaro.setDescripcionProducto("Servidor de alta gama");
        productoCaro.setPrecioProducto(50000.00);

        when(productoRepository.save(productoCaro)).thenReturn(productoCaro);

        // When
        Producto resultado = productoService.crearProducto(productoCaro);

        // Then
        assertThat(resultado.getPrecioProducto()).isEqualTo(50000.00);
        verify(productoRepository, times(1)).save(productoCaro);
    }

    @Test
    @DisplayName("Debe crear producto con precio cero")
    void debeCrearProductoConPrecioCero() {
        // Given
        Producto productoGratis = new Producto();
        productoGratis.setNombreProducto("Muestra gratis");
        productoGratis.setDescripcionProducto("Producto de muestra");
        productoGratis.setPrecioProducto(0.0);

        when(productoRepository.save(productoGratis)).thenReturn(productoGratis);

        // When
        Producto resultado = productoService.crearProducto(productoGratis);

        // Then
        assertThat(resultado.getPrecioProducto()).isEqualTo(0.0);
        verify(productoRepository, times(1)).save(productoGratis);
    }

    @Test
    @DisplayName("Debe crear producto con nombre muy largo")
    void debeCrearProductoConNombreMuyLargo() {
        // Given
        String nombreLargo = faker.lorem().characters(200);
        Producto productoNombreLargo = new Producto();
        productoNombreLargo.setNombreProducto(nombreLargo);
        productoNombreLargo.setDescripcionProducto("Descripción normal");
        productoNombreLargo.setPrecioProducto(100.0);

        when(productoRepository.save(productoNombreLargo)).thenReturn(productoNombreLargo);

        // When
        Producto resultado = productoService.crearProducto(productoNombreLargo);

        // Then
        assertThat(resultado.getNombreProducto()).isEqualTo(nombreLargo);
        verify(productoRepository, times(1)).save(productoNombreLargo);
    }

    @Test
    @DisplayName("Debe crear producto con descripción muy larga")
    void debeCrearProductoConDescripcionMuyLarga() {
        // Given
        String descripcionLarga = faker.lorem().characters(500);
        Producto productoDescripcionLarga = new Producto();
        productoDescripcionLarga.setNombreProducto("Producto Test");
        productoDescripcionLarga.setDescripcionProducto(descripcionLarga);
        productoDescripcionLarga.setPrecioProducto(100.0);

        when(productoRepository.save(productoDescripcionLarga)).thenReturn(productoDescripcionLarga);

        // When
        Producto resultado = productoService.crearProducto(productoDescripcionLarga);

        // Then
        assertThat(resultado.getDescripcionProducto()).isEqualTo(descripcionLarga);
        verify(productoRepository, times(1)).save(productoDescripcionLarga);
    }

    // ================ TESTS PARA OBTENER TODOS ================

    @Test
    @DisplayName("Debe obtener todos los productos cuando existen")
    void debeObtenerTodosLosProductos() {
        // Given
        when(productoRepository.findAll()).thenReturn(productoList);

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(15);
        assertThat(resultado).containsExactlyElementsOf(productoList);
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay productos")
    void debeRetornarListaVaciaCuandoNoHayProductos() {
        // Given
        when(productoRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener productos con diferentes precios")
    void debeObtenerProductosConDiferentesPrecios() {
        // Given
        List<Producto> productosVariados = Arrays.asList(
                crearProducto(1L, "Producto Barato", 1.0),
                crearProducto(2L, "Producto Medio", 100.0),
                crearProducto(3L, "Producto Caro", 10000.0)
        );
        when(productoRepository.findAll()).thenReturn(productosVariados);

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getPrecioProducto()).isEqualTo(1.0);
        assertThat(resultado.get(1).getPrecioProducto()).isEqualTo(100.0);
        assertThat(resultado.get(2).getPrecioProducto()).isEqualTo(10000.0);
    }

    // ================ TESTS PARA OBTENER POR ID ================

    @Test
    @DisplayName("Debe obtener producto por ID exitosamente")
    void debeObtenerProductoPorIdExitosamente() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));

        // When
        Producto resultado = productoService.traerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        assertThat(resultado.getNombreProducto()).isEqualTo(productoPrueba.getNombreProducto());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar ProductoException cuando producto no existe por ID")
    void debeLanzarExcepcionCuandoProductoNoExistePorId() {
        // Given
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.traerPorId(999L))
                .isInstanceOf(ProductoException.class)
                .hasMessageContaining("El producto con el id999no existe");

        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener producto con ID muy alto")
    void debeObtenerProductoConIdMuyAlto() {
        // Given
        Long idMuyAlto = 9999999L;
        Producto productoIdAlto = crearProducto(idMuyAlto, "Producto ID Alto", 100.0);
        when(productoRepository.findById(idMuyAlto)).thenReturn(Optional.of(productoIdAlto));

        // When
        Producto resultado = productoService.traerPorId(idMuyAlto);

        // Then
        assertThat(resultado.getIdProducto()).isEqualTo(idMuyAlto);
        verify(productoRepository, times(1)).findById(idMuyAlto);
    }

    // ================ TESTS PARA ACTUALIZAR PRODUCTO ================

    @Test
    @DisplayName("Debe actualizar producto exitosamente")
    void debeActualizarProductoExitosamente() {
        // Given
        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Nombre Actualizado");
        datosActualizacion.setDescripcionProducto("Descripción Actualizada");
        datosActualizacion.setPrecioProducto(200.0);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertThat(resultado.getNombreProducto()).isEqualTo("Nombre Actualizado");
        assertThat(resultado.getDescripcionProducto()).isEqualTo("Descripción Actualizada");
        assertThat(resultado.getPrecioProducto()).isEqualTo(200.0);

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar ProductoException cuando producto no existe para actualizar")
    void debeLanzarExcepcionCuandoProductoNoExisteParaActualizar() {
        // Given
        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Nuevo Nombre");

        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.actualizarProducto(999L, datosActualizacion))
                .isInstanceOf(ProductoException.class)
                .hasMessageContaining("El medico on el id999no existe");

        verify(productoRepository, times(1)).findById(999L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el nombre del producto")
    void debeActualizarSoloElNombreDelProducto() {
        // Given
        String nombreOriginal = productoPrueba.getNombreProducto();
        String descripcionOriginal = productoPrueba.getDescripcionProducto();
        Double precioOriginal = productoPrueba.getPrecioProducto();

        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Solo Nombre Cambiado");
        datosActualizacion.setDescripcionProducto(null);
        datosActualizacion.setPrecioProducto(0.0);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertThat(resultado.getNombreProducto()).isEqualTo("Solo Nombre Cambiado");
        assertThat(resultado.getDescripcionProducto()).isNull(); // Se actualiza a null
        assertThat(resultado.getPrecioProducto()).isEqualTo(0.0); // Se actualiza a 0
    }

    @Test
    @DisplayName("Debe actualizar producto con precio negativo")
    void debeActualizarProductoConPrecioNegativo() {
        // Given
        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto("Producto Con Descuento");
        datosActualizacion.setDescripcionProducto("Precio negativo por descuento");
        datosActualizacion.setPrecioProducto(-50.0);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertThat(resultado.getPrecioProducto()).isEqualTo(-50.0);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe actualizar producto con valores extremos")
    void debeActualizarProductoConValoresExtremos() {
        // Given
        Producto datosActualizacion = new Producto();
        datosActualizacion.setNombreProducto(""); // Nombre vacío
        datosActualizacion.setDescripcionProducto(""); // Descripción vacía
        datosActualizacion.setPrecioProducto(Double.MAX_VALUE); // Precio máximo

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion);

        // Then
        assertThat(resultado.getNombreProducto()).isEmpty();
        assertThat(resultado.getDescripcionProducto()).isEmpty();
        assertThat(resultado.getPrecioProducto()).isEqualTo(Double.MAX_VALUE);
    }

    // ================ TESTS PARA ELIMINAR PRODUCTO ================

    @Test
    @DisplayName("Debe eliminar producto exitosamente cuando existe")
    void debeEliminarProductoExitosamenteCuandoExiste() {
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
    @DisplayName("No debe hacer nada cuando se intenta eliminar producto inexistente")
    void noDebeHacerNadaCuandoSeIntentaEliminarProductoInexistente() {
        // Given
        when(productoRepository.existsById(999L)).thenReturn(false);

        // When
        productoService.eliminarProducto(999L);

        // Then
        verify(productoRepository, times(1)).existsById(999L);
        verify(productoRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("Debe verificar existencia antes de eliminar")
    void debeVerificarExistenciaAntesDeEliminar() {
        // Given
        Long idProducto = 5L;
        when(productoRepository.existsById(idProducto)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(idProducto);

        // When
        productoService.eliminarProducto(idProducto);

        // Then
        // Verificar que primero se verifica existencia
        verify(productoRepository, times(1)).existsById(idProducto);
        // Y luego se elimina
        verify(productoRepository, times(1)).deleteById(idProducto);
    }

    @Test
    @DisplayName("Debe eliminar producto con ID muy alto")
    void debeEliminarProductoConIdMuyAlto() {
        // Given
        Long idMuyAlto = 9999999L;
        when(productoRepository.existsById(idMuyAlto)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(idMuyAlto);

        // When
        productoService.eliminarProducto(idMuyAlto);

        // Then
        verify(productoRepository, times(1)).existsById(idMuyAlto);
        verify(productoRepository, times(1)).deleteById(idMuyAlto);
    }

    // ================ TESTS DE CASOS LÍMITE Y ESPECIALES ================

    @Test
    @DisplayName("Debe manejar productos con caracteres especiales en nombre")
    void debeManejarnProductosConCaracteresEspecialesEnNombre() {
        // Given
        Producto productoEspecial = new Producto();
        productoEspecial.setNombreProducto("Ñandú & Café (100%) - ¿Especial?");
        productoEspecial.setDescripcionProducto("Descripción normal");
        productoEspecial.setPrecioProducto(25.99);

        when(productoRepository.save(productoEspecial)).thenReturn(productoEspecial);

        // When
        Producto resultado = productoService.crearProducto(productoEspecial);

        // Then
        assertThat(resultado.getNombreProducto()).isEqualTo("Ñandú & Café (100%) - ¿Especial?");
        verify(productoRepository, times(1)).save(productoEspecial);
    }

    @Test
    @DisplayName("Debe manejar productos con precios decimales complejos")
    void debeManejarnProductosConPreciosDecimalesComplejos() {
        // Given
        Producto producto1 = crearProducto(1L, "Producto 1", 99.999);
        Producto producto2 = crearProducto(2L, "Producto 2", 0.001);
        Producto producto3 = crearProducto(3L, "Producto 3", 1234.5678);

        List<Producto> productos = Arrays.asList(producto1, producto2, producto3);
        when(productoRepository.findAll()).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.traerTodo();

        // Then
        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getPrecioProducto()).isEqualTo(99.999);
        assertThat(resultado.get(1).getPrecioProducto()).isEqualTo(0.001);
        assertThat(resultado.get(2).getPrecioProducto()).isEqualTo(1234.5678);
    }

    @Test
    @DisplayName("Debe obtener producto que fue actualizado múltiples veces")
    void debeObtenerProductoQueEueActualizadoMultiplesVeces() {
        // Given
        // Simulamos múltiples actualizaciones
        Producto productoOriginal = crearProducto(1L, "Original", 100.0);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoOriginal));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Primera actualización
        Producto datosActualizacion1 = new Producto();
        datosActualizacion1.setNombreProducto("Primera Actualización");
        datosActualizacion1.setDescripcionProducto("Desc 1");
        datosActualizacion1.setPrecioProducto(200.0);
        
        productoService.actualizarProducto(1L, datosActualizacion1);

        // When - Segunda actualización
        Producto datosActualizacion2 = new Producto();
        datosActualizacion2.setNombreProducto("Segunda Actualización");
        datosActualizacion2.setDescripcionProducto("Desc 2");
        datosActualizacion2.setPrecioProducto(300.0);
        
        Producto resultado = productoService.actualizarProducto(1L, datosActualizacion2);

        // Then
        assertThat(resultado.getNombreProducto()).isEqualTo("Segunda Actualización");
        assertThat(resultado.getPrecioProducto()).isEqualTo(300.0);
        verify(productoRepository, times(2)).findById(1L);
        verify(productoRepository, times(2)).save(any(Producto.class));
    }

    // ================ MÉTODOS AUXILIARES ================

    private Producto crearProducto(Long id, String nombre, Double precio) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombreProducto(nombre);
        producto.setDescripcionProducto("Descripción de " + nombre);
        producto.setPrecioProducto(precio);
        return producto;
    }
} 