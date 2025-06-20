package com.ampuero.msvc.boleta.controllers;

import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import com.ampuero.msvc.boleta.dtos.ErrorDTO;
import com.ampuero.msvc.boleta.dtos.MontoUpdateRequestDTO;
import com.ampuero.msvc.boleta.models.entities.Boleta;
import com.ampuero.msvc.boleta.services.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Boleta API",
        description = "Aqui se generan todos los metodos CRUD para Boleta"
)
public class BoletaController {

    private final BoletaService boletaService;
    // POST: Crear nueva boleta
    @PostMapping
    @Operation(
            summary = "Endpoint guardado de una boleta",
            description = "Endpoint que me permite capturar un elemento de tipo BoletaResponseDTO.Class " +
                    "y lo guarda dentro de nuestra base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Creacion exitosa"
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
    public ResponseEntity<BoletaResponseDTO> crearBoleta(@Valid @RequestBody BoletaDTO boletaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boletaService.crearBoleta(boletaDTO));
    }

    // GET: Obtener todas las boletas
    @GetMapping
    @Operation(
            summary = "Endpoint que obtiene todas las boletas",
            description = "Este endpoint devuelve un List con todas las boletas que esten registrados " +
                    "en la base de datos junto a su respectivo cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operacion de extraccion de boletas exitosa"
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
    public ResponseEntity<List<BoletaResponseDTO>> obtenerTodas() {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerTodas());
    }

    // GET: Obtener boletas por ID de clientes
    @GetMapping("/clientes/{idCliente}")
    @Operation(
            summary = "Endpoint que devuelve una boleta por id del cliente",
            description = "Endpoint que va a devolver un elemento de BoletaResponseDTO al momento de buscarlo por el " +
                    "numero identificador de Cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Obtencion de Boleta por id de Cliente Exitosa"
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
    public ResponseEntity<List<BoletaResponseDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerPorCliente(idCliente));
    }

    // PUT: Actualizar total de boleta por ID
    @PutMapping("/{id}/total")
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
                    description = "Error cuando el cuerpo de la solicitud no cumple con los requisitos de validación"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error cuando la boleta con cierto ID no existe"
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    description = "Primary Key - Entidad Boleta",
                    required = true
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura de datos que me permite actualizar el monto de una boleta",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MontoUpdateRequestDTO.class)
            )
    )
    public ResponseEntity<Void> actualizarTotalBoleta(
            @PathVariable Long id,
            @Valid @RequestBody MontoUpdateRequestDTO montoDTO) {
        boletaService.actualizarTotalBoleta(id, montoDTO.getMonto());
        return ResponseEntity.ok().build();
    }

    // GET: Obtener boleta por ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Endpoint que devuelve una boleta por id",
            description = "Endpoint que va a devolver un elemento de Boleta al momento de buscarlo por su numero identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Obtencion por id correcta"
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
    public ResponseEntity<BoletaResponseDTO> obtenerBoletaPorId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerBoletaPorId(id));
    }

    // DELETE: Eliminar boleta por ID (usa idFactura como path variable)
    @DeleteMapping("/{idFactura}")
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
                    name = "id",
                    description = "Primary Key - Entidad Boleta",
                    required = true
            )
    })
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long idFactura) {
        boletaService.eliminarBoleta(idFactura);
        return ResponseEntity.noContent().build();
    }
}