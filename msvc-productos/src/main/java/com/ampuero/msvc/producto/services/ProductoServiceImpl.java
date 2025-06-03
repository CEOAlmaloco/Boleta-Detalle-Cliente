package com.ampuero.msvc.producto.services;

import com.ampuero.msvc.producto.exceptions.ProductoException;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

//<>
@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

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
