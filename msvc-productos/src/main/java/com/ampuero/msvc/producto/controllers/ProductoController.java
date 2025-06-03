package com.ampuero.msvc.producto.controllers;


import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Validated
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET: Traer todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> traerTodos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productoService.traerTodo());
    }

    // GET: Traer producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> traerPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.productoService.traerPorId(id));
    }
    // POST: Crear nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody @Valid Producto producto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.productoService.crearProducto(producto));
    }
    // PUT: Actualizar producto por ID
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.OK).body(productoService.actualizarProducto(id, producto));
    }
    // DELETE: Eliminar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
       productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

}
