package com.ampuero.msvc.detalle.clients;

import com.ampuero.msvc.detalle.models.ProductoPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-productos", url = "http://localhost:8084")
public interface ProductoClient {
    @GetMapping("/api/v1/productos/{id}")
    ResponseEntity<ProductoPojo> getProductoById(@PathVariable Long id);
}