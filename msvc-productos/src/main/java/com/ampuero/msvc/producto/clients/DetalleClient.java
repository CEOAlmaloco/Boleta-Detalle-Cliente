package com.ampuero.msvc.producto.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "msvc-detalles", url = "http://localhost:8083")
public interface DetalleClient {

}