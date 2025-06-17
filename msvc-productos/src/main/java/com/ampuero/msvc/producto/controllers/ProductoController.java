package com.ampuero.msvc.producto.controllers;


import com.ampuero.msvc.producto.dtos.ErrorDTO;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Producto API",
description = "Aqui se generan todos los metodos crud para producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET: Traer todos los productos
    @GetMapping
    @Operation(
            summary = "metodo que obtiene todos los productos",
            description = "este endpoint devuleve todos los productos que se encuentren" +
                    "en la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "operacion de extraccion de productos exitosa")
    })
    public ResponseEntity<List<Producto>> traerTodos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productoService.traerTodo());
    }

    // GET: Traer producto por ID
    @GetMapping("/{id}")
    @Operation(
            summary = "endpoint que devuelve un producto por id",
            description = "endpoint que devuleve un producto.class al momento de buscarlo por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "obtencion por id correcta"),
            @ApiResponse(responseCode = "404",
            description = "error el producto con esa id no existe")

    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "primary KEY - entidad producto",
                    content = @Content(
                            mediaType = "application/json",
                            //schema = @Schema(implementation = ErrorDTO.class)
                            examples = @ExampleObject(
                                    name = "Error no encontrado",
                                    value = "{\"status\":\"200\",\"error\":\"medico no encontrado\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Producto> traerPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.productoService.traerPorId(id));
    }
    // POST: Crear nuevo producto
    @PostMapping
    @Operation(
            summary = "endpoint guardado de un medico",
            description = "endpoint que permite capturar un elemento producto.class y lo guarda"+
                    "dentro de la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Creación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class) // O tu clase de respuesta exitosa
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Algún elemento del microservicio no se encuentra",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El elemento que intentaste crear ya existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "estructura de datos que me permite realizar la creacion de un producto",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
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
