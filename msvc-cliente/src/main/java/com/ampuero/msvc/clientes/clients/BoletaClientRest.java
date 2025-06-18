package com.ampuero.msvc.clientes.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "msvc-boletas", url = "http://localhost:8081")
public interface BoletaClientRest {
}
