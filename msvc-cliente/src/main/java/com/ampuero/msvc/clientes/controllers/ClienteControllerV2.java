package com.ampuero.msvc.clientes.controllers;

import com.ampuero.msvc.clientes.assemblers.ClienteModelAssembler;
import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
import com.ampuero.msvc.clientes.dtos.ErrorDTO;
import com.ampuero.msvc.clientes.models.Cliente;
import com.ampuero.msvc.clientes.services.ClienteService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("api/v2/clientes")
@Validated
@Tag(
        name = "Cliente API HATEOAS",
        description = "Aqui se generan todos los metodos CRUD para cliente"
)
public class ClienteControllerV2 {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteModelAssembler clienteModelAssembler;

    @PostMapping
    @Operation(
            summary = "Endpoint guardado de un cliente",
            description = "Endpoint que me permite capturar un elemento Cliente.class y lo guarda " +
                    "dentro de nuestra base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Creacion exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Cliente.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Algun elemento de un msvc no se encuentra",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El elemento que intentas crear ya existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataIntegrityViolationException.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura de datos que me permite realizar la creacion de un cliente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClienteCreationDTO.class)
            )
    )
    public ResponseEntity<EntityModel<Cliente>> crearCliente(@Valid @RequestBody ClienteCreationDTO clienteCreationDTO){
        Cliente clienteNew = this.clienteService.crearCliente(clienteCreationDTO);
        EntityModel<Cliente> entityModel = this.clienteModelAssembler.toModel(clienteNew);
        return ResponseEntity
                .created(linkTo(methodOn(ClienteControllerV2.class).traerCliente(clienteNew.getIdUsuario())).toUri())
                .body(entityModel);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
            summary = "Endpoint que obtiene todos los clientes",
            description = "Este endpoint devuelve un List con todos los clientes que esten registrados " +
                    "en la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion de extraccion de clientes exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Cliente.class)
                    )
            )
    })
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> traerTodos() {

        List<EntityModel<Cliente>> entityModels = this.clienteService.traerTodos()
                .stream()
                .map(clienteModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Cliente>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ClienteControllerV2.class).traerTodos()).withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
            summary = "Endpoint que devuelve un cliente por id",
            description = "Endpoint que va a devolver un elemento de Cliente al momento de buscarlo por su numero identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Obtencion por id correcta",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Cliente.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando el cliente con cierto ID no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "Primary Key - Entidad Cliente",
                    required = true
            )
    })
    public ResponseEntity<EntityModel<Cliente>> traerCliente(@PathVariable Long id){
        EntityModel<Cliente> entityModel = this.clienteModelAssembler.toModel(
                this.clienteService.traerPorId(id)
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
    }

    // PUT: Actualizar clientes por ID

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
            summary = "Endpoint que actualiza un cliente por id",
            description = "Endpoint que va a actualizar los parametros de un cliente " +
                    "al momento de buscarlo por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actualizacion por ID exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = Cliente.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando el cliente con cierto ID no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "Primary Key - Entidad Cliente",
                    required = true
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura de datos que me permite realizar la creación de un cliente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClienteCreationDTO.class)
            )
    )
    public ResponseEntity<EntityModel<Cliente>> actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente){

        Cliente clienteActualizado = this.clienteService.actualizarCliente(id, cliente);
        EntityModel<Cliente> entityModel = this.clienteModelAssembler.toModel(clienteActualizado);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
    }

    // DELETE: Eliminar clientes por ID
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Endpoint que elimina un cliente por ID",
            description = "Endpoint que va a eliminar un cliente al momento de " +
                    "buscarlo por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente eliminado correctamente (sin contenido de respuesta)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado con el ID indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "Primary Key - Entidad Cliente",
                    required = true
            )
    })
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id){
        clienteService.eliminarCliente(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
