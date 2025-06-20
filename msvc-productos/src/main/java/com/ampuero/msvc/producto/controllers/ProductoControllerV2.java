package com.ampuero.msvc.producto.controllers;

import com.ampuero.msvc.producto.assemblers.ProductoModelAssembler;
import com.ampuero.msvc.producto.dtos.ErrorDTO;
import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/productos")
@Validated
@Tag(name = "ProductoV2", description = "Operaciones CRUD de productos hateoas")
public class ProductoControllerV2 {
    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler productoModelAssembler;


    @GetMapping
    @Operation(summary = "Obtiene todos los productos", description = "Devuelve una Lista de productos en el Body")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion existosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Producto.class)
                    )
            )
    })
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> traerTodos() {
        List<EntityModel<Producto>> productos = productoService.traerTodo()
                .stream()
                .map(productoModelAssembler::toModel)
                .collect(Collectors.toList());
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CollectionModel.of(productos));
    }

    // GET: Traer producto por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto", description = "A través del id suministrado devuelve el producto con esa id")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion existosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "producto no encontrado, con el id suministrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema =  @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(name="id", description = "Este es el id unico del producto", required = true)
    })
    public ResponseEntity<EntityModel<Producto>> traerPorId(@PathVariable Long id){
        Producto producto = this.productoService.traerPorId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productoModelAssembler.toModel(producto));
    }
    // POST: Crear nuevo producto
    @PostMapping
    @Operation(
            summary = "Guarda un producto",
            description = "Con este método podemos enviar los datos mediante un body y realizar el guardado en la base de datos H2"
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Guardado exitoso",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El producto guardado ya se encuentra en la base de datos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Producto por crear",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
    public ResponseEntity<EntityModel<Producto>> crearProducto(@RequestBody @Valid Producto producto){
        Producto nuevoProducto = this.productoService.crearProducto(producto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoModelAssembler.toModel(nuevoProducto));
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
