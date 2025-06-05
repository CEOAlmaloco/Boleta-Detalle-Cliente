package com.ampuero.msvc.boleta.controllers;

import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import com.ampuero.msvc.boleta.dtos.MontoUpdateRequestDTO;
import com.ampuero.msvc.boleta.services.BoletaService;
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
public class BoletaController {

    private final BoletaService boletaService;
//cinebtario
    // POST: Crear nueva boleta
    @PostMapping
    public ResponseEntity<BoletaResponseDTO> crearBoleta(@Valid @RequestBody BoletaDTO boletaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boletaService.crearBoleta(boletaDTO));
    }

    // GET: Obtener todas las boletas
    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> obtenerTodas() {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerTodas());
    }

    // GET: Obtener boletas por ID de cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<BoletaResponseDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerPorCliente(idCliente));
    }

    // PUT: Actualizar total de boleta por ID
    @PutMapping("/{id}/total")
    public ResponseEntity<Void> actualizarTotalBoleta(
            @PathVariable Long id,
            @Valid @RequestBody MontoUpdateRequestDTO montoDTO) {
        boletaService.actualizarTotalBoleta(id, montoDTO.getMonto());
        return ResponseEntity.ok().build();
    }

    // GET: Obtener boleta por ID
    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> obtenerBoletaPorId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(boletaService.obtenerBoletaPorId(id));
    }

    // DELETE: Eliminar boleta por ID (usa idFactura como path variable)
    @DeleteMapping("/{idFactura}")
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long idFactura) {
        boletaService.eliminarBoleta(idFactura);
        return ResponseEntity.noContent().build();
    }
}