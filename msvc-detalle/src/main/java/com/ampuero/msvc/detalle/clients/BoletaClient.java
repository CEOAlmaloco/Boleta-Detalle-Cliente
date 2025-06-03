package com.ampuero.msvc.detalle.clients;

import com.ampuero.msvc.detalle.models.BoletaPojo;
import com.ampuero.msvc.detalle.dtos.MontoUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-boletas", url = "http://localhost:8081")
public interface BoletaClient {
    @GetMapping("/api/v1/boletas/{id}")
    ResponseEntity<BoletaPojo> getBoletaById(@PathVariable Long id);

    @PutMapping("/api/v1/boletas/{idBoleta}/total")
    void actualizarTotalBoleta(
            @PathVariable("idBoleta") Long idBoleta,
            @RequestBody MontoUpdateRequestDTO montoDTO);
}