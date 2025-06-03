package com.ampuero.msvc.clientes.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "msvc-boletas", url = "http://localhost:8081")
public interface BoletaClientRest {

    //este lo cree pq pense q habia un error XD
}
