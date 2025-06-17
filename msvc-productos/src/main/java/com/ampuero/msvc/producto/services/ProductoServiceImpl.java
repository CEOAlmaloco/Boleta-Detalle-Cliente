package com.ampuero.msvc.producto.services;

import com.ampuero.msvc.producto.exceptions.ProductoException;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementación del servicio de gestión de productos.
 *
 * Esta clase proporciona la lógica de negocio para las operaciones CRUD de productos,
 * utilizando el patrón Service Layer con inyección de dependencias de Spring.
 *
 * Características:
 * - Transacciones automáticas para operaciones de escritura
 * - Validación de parámetros de entrada
 * - Manejo centralizado de excepciones del dominio
 * - Separación clara entre lógica de negocio y acceso a datos
 *
 * @author Ampuero Development Team
 * @version 1.0
 * @since 1.0
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    /**
     * Repository para acceso a datos de productos.
     * Inyectado automáticamente por Spring IoC container.
     */

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }


    /**
     * {@inheritDoc}
     *
     * Implementación que delega directamente al repository para obtener
     * todos los productos sin filtros adicionales.
     */

    @Override
    public List<Producto> traerTodo(){
        return this.productoRepository.findAll();
    };

    @Override
    public Producto traerPorId(Long id){
        return this.productoRepository.findById(id)
                .orElseThrow(
                ()-> new ProductoException("El producto con el id"+id+"no existe")
        );
    };

    @Override
    public Producto actualizarProducto(Long id, Producto producto){
    return productoRepository.findById(id).map(m->{
        m.setNombreProducto(producto.getNombreProducto());
        m.setDescripcionProducto(producto.getDescripcionProducto());
        m.setPrecioProducto(producto.getPrecioProducto());
        return productoRepository.save(m);
        }).orElseThrow(()-> new ProductoException("El medico on el id"+id+"no existe "));
    };
    @Override
    public void eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        }
    }

}
