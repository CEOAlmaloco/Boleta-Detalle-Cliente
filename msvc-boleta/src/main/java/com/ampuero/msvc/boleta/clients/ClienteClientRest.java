package com.ampuero.msvc.boleta.clients;

import com.ampuero.msvc.boleta.dtos.ClienteResponseDTO;
import com.ampuero.msvc.boleta.models.ClientePojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// clients/ClienteClientRest.java
// En msvc-detalle (ClienteClientRest.java)
@FeignClient(name = "msvc-clientes", url = "http://localhost:8082")
public interface ClienteClientRest {
    @GetMapping("/api/v1/clientes/{id}")
    ClienteResponseDTO findClienteById(@PathVariable Long id); // Debe devolver ClienteResponseDTO
}