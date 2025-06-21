package com.ampuero.msvc.boleta.controllers;

import com.ampuero.msvc.boleta.assemblers.BoletaResponseDTOModelAssembler;
import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import com.ampuero.msvc.boleta.dtos.ErrorDTO;
import com.ampuero.msvc.boleta.dtos.MontoUpdateRequestDTO;
import com.ampuero.msvc.boleta.services.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("api/v2/boletas")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Boleta API HATEOAS",
        description = "Aqui se generan todos los metodos CRUD para boleta"
)
public class BoletaControllerV2 {

    @Autowired
    private BoletaResponseDTOModelAssembler boletaResponseDTOModelAssembler;

    private final BoletaService boletaService;

    //POST: Creacion de Boleta
    @PostMapping
    @Operation(
            summary = "Endpoint guardado de una boleta",
            description = "Endpoint que me permite capturar un elemento de tipo BoletaResponseDTO.Class " +
                    "y lo guarda dentro de nuestra base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Creacion exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = BoletaResponseDTO.class)
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
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura de datos que me permite realizar la creacion de una boleta",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BoletaDTO.class)
            )
    )
    public ResponseEntity<EntityModel<BoletaResponseDTO>> crearBoleta(@Valid @RequestBody BoletaDTO boletaDTO) {
        BoletaResponseDTO boletaResponseDTONew = this.boletaService.crearBoleta(boletaDTO);
        EntityModel<BoletaResponseDTO> entityModel = this.boletaResponseDTOModelAssembler.toModel(boletaResponseDTONew);
        return ResponseEntity
                .created(linkTo(methodOn(BoletaControllerV2.class).obtenerBoletaPorId(boletaResponseDTONew.getIdBoleta())).toUri())
                .body(entityModel);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
            summary = "Endpoint que obtiene todas las boletas",
            description = "Este endpoint devuelve un List con todas las boletas que esten registrados " +
                    "en la base de datos junto a su respectivo cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion de extraccion de boletas exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = BoletaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando en el List no existe ninguna boleta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<CollectionModel<EntityModel<BoletaResponseDTO>>> obtenerTodas() {
        List<EntityModel<BoletaResponseDTO>> entityModels = this.boletaService.obtenerTodas()
                .stream()
                .map(boletaResponseDTOModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<BoletaResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(BoletaControllerV2.class).obtenerTodas()).withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
    }

    // GET: Obtener boletas por ID de clientes
    @GetMapping("/clientes/{idCliente}")
    @Operation(
            summary = "Endpoint que devuelve todas las boletas de un id del cliente",
            description = "Endpoint que va a devolver elementos BoletaResponseDTO al momento de buscarlo por el " +
                    "numero identificador de Cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion de extraccion de boletas por ID de cliente exitosa",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = BoletaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando no se encuentran boletas asociadas al ID del Cliente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "idCliente",
                    description = "Primary Key - Entidad Cliente",
                    required = true
            )
    })
    public ResponseEntity<CollectionModel<EntityModel<BoletaResponseDTO>>> obtenerPorCliente(@PathVariable Long idCliente) {
        List<EntityModel<BoletaResponseDTO>> entityModels = this.boletaService.obtenerPorCliente(idCliente)
                .stream()
                .map(boletaResponseDTOModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<BoletaResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(BoletaControllerV2.class).obtenerPorCliente(idCliente)).withRel("boletas-cliente")
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
    }

    // GET: Obtener boleta por ID
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
            summary = "Endpoint que devuelve una boleta por id",
            description = "Endpoint que va a devolver un elemento de Boleta al momento de buscarlo por su numero identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Obtencion por id correcta",
                    content = @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = BoletaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando la boleta con cierto ID no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "Primary Key - Entidad Boleta",
                    required = true
            )
    })
    public ResponseEntity<EntityModel<BoletaResponseDTO>> obtenerBoletaPorId(@PathVariable Long id) {
        EntityModel<BoletaResponseDTO> entityModel = this.boletaResponseDTOModelAssembler.toModel(
                this.boletaService.obtenerBoletaPorId(id)
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
    }
    // PUT: Actualizar total de boleta por ID
    @PutMapping("/{idBoleta}/total")
    @Operation(
            summary = "Endpoint que actualiza el total de una boleta por id",
            description = "Endpoint que va a actualizar el total de una boleta " +
                    "al momento de buscarla por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actualizacion del total de Boleta Exitosa"

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error cuando el monto de la boleta es negativo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Monto Negativo",
                                    summary = "Monto inválido",
                                    value = """
                                        {
                                            "codigo": "400",
                                            "mensaje": "monto: El monto debe ser mayor a 0"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando la boleta con cierto ID no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )

    })
    @Parameters(value = {
            @Parameter(
                    name = "idBoleta",
                    description = "Primary Key - Entidad Boleta",
                    required = true
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura de datos que me permite actualizar el monto de una boleta",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MontoUpdateRequestDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "Monto Valido",
                                    summary = "Monto positivo válido",
                                    value = """
                                        {
                                            "monto": 1500.0
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "MontoNegativo",
                                    summary = "Monto negativo inválido",
                                    value = """
                                        {
                                            "monto": -250.0
                                        }
                                        """
                            )
                    }
            )
    )
    public ResponseEntity<Void> actualizarTotalBoleta(
            @PathVariable Long idBoleta,
            @Valid @RequestBody MontoUpdateRequestDTO montoDTO) {
        boletaService.actualizarTotalBoleta(idBoleta, montoDTO.getMonto());
        return ResponseEntity.ok().build();
    }
    // DELETE: Eliminar boleta por ID (usa idFactura como path variable)
    @DeleteMapping("/{idBoleta}")
    @Operation(
            summary = "Endpoint que elimina una boleta por ID",
            description = "Endpoint que va a eliminar una boleta al momento de " +
                    "buscarla por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Boleta eliminada correctamente (sin contenido de respuesta)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando la boleta con cierto id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "idBoleta",
                    description = "Primary Key - Entidad Boleta",
                    required = true
            )
    })
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long idBoleta) {
        boletaService.eliminarBoleta(idBoleta);
        return ResponseEntity
                .noContent()
                .build();
    }
}
