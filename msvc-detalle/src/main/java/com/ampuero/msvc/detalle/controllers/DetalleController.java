package com.ampuero.msvc.detalle.controllers;

import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.services.DetalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalles")
@RequiredArgsConstructor
@Validated
public class DetalleController {

    private final DetalleService detalleService;

    // POST: Crear nuevo detalle de boleta
    @PostMapping
    public ResponseEntity<DetalleResponseDTO> crearDetalle(@Valid @RequestBody DetalleDTO detalleDTO) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.detalleService.crearDetalle(detalleDTO));
    }

    // GET: Obtener detalles por ID de boleta
    @GetMapping("/boleta/{idBoleta}")
    public ResponseEntity<List<DetalleResponseDTO>> obtenerPorBoleta(@PathVariable Long idBoleta) {
        return ResponseEntity.status(HttpStatus.OK).body(detalleService.obtenerPorBoleta(idBoleta));
    }

    // PUT: Actualizar detalle por ID
    @PutMapping("/{idDetalle}")
    public ResponseEntity<DetalleResponseDTO> actualizarDetalle(
            @PathVariable Long idDetalle,
            @Valid @RequestBody DetalleDTO detalleDTO) throws DetalleException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(detalleService.actualizarDetalle(idDetalle, detalleDTO));
    }

    // DELETE: Eliminar detalle por ID
    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long idDetalle) throws ResourceNotFoundException {
        detalleService.eliminarDetalle(idDetalle);
        return ResponseEntity.noContent().build();
    }
}
