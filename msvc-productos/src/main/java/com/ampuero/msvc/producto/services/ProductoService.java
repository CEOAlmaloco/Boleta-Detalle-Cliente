package com.ampuero.msvc.producto.services;

import com.ampuero.msvc.producto.models.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> traerTodo();
    Producto traerPorId(Long id);
    Producto crearProducto(Producto producto);
    Producto actualizarProducto(Long id, Producto producto);
    void eliminarProducto(Long id);
}
