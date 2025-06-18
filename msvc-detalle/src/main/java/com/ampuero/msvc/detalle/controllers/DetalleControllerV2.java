package com.ampuero.msvc.detalle.controllers;

import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.dtos.ErrorDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.services.DetalleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class DetalleControllerV2 {

    @Autowired
    private DetalleService detalleService;

    // POST: Crear nuevo detalle de boleta
    @PostMapping
    @Operation(summary = "Crear un nuevo detalle", description = "Crea un nuevo registro de detalle asociado a una boleta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso relacionado no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<DetalleResponseDTO> crearDetalle(@Valid @RequestBody DetalleDTO detalleDTO) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.detalleService.crearDetalle(detalleDTO));
    }


    @GetMapping("/boleta/{idBoleta}")
    @Operation(summary = "Obtiene un detalle", description = "A través del id suministrado devuelve el detalle con esa id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Operacion existosa"),
            @ApiResponse(
                    responseCode = "404",
                    description = "detalle no encontrado, con el id suministrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema =  @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(name="id", description = "Este es el id unico del detalle", required = true)
    })
    public ResponseEntity<List<DetalleResponseDTO>> obtenerPorBoleta(@PathVariable Long idBoleta) {
        return ResponseEntity.status(HttpStatus.OK).body(detalleService.obtenerPorBoleta(idBoleta));
    }

    // PUT: Actualizar detalle por ID
    @PutMapping("/{idDetalle}")
    @Operation(summary = "Actualizar un detalle", description = "Actualiza los datos de un detalle específico según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud con errores de validación",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "idDetalle", description = "ID único del detalle a actualizar", required = true)
    })
    public ResponseEntity<DetalleResponseDTO> actualizarDetalle(
            @PathVariable Long idDetalle,
            @Valid @RequestBody DetalleDTO detalleDTO) throws DetalleException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(detalleService.actualizarDetalle(idDetalle, detalleDTO));
    }

    // DELETE: Eliminar detalle por ID
    @DeleteMapping("/{idDetalle}")
    @Operation(summary = "Eliminar un detalle", description = "Elimina un detalle específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "idDetalle", description = "ID único del detalle a eliminar", required = true)
    })
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long idDetalle) throws ResourceNotFoundException {
        detalleService.eliminarDetalle(idDetalle);
        return ResponseEntity.noContent().build();
    }
}
