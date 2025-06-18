package com.ampuero.msvc.detalle.clients;

/**
 * ProductoClient.java
 *
 * Descripción:
 * Interfaz que define el cliente Feign para comunicarse con el microservicio de productos
 * (`msvc-productos`). Permite realizar llamadas HTTP REST de manera declarativa desde
 * el microservicio de detalle para obtener información sobre productos.
 *
 * Funcionalidad:
 * - Recupera un producto específico a través de su ID.
 * - Utiliza una llamada GET al endpoint remoto: /api/v1/productos/{id}.
 * - Recibe como respuesta un `ProductoPojo` dentro de un `ResponseEntity`.
 *
 * Configuración:
 * - Anotado con `@FeignClient`, se especifica el nombre lógico del servicio y su URL base.
 * - Es compatible con balanceadores de carga si se usa con Eureka y se omite la `url`.
 *
 * Uso típico:
 * - Obtener el precio, nombre y descripción del producto al crear o modificar un detalle.
 * - Verificar la existencia del producto antes de persistir el detalle.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 */

import com.ampuero.msvc.detalle.models.ProductoPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-productos", url = "http://localhost:8084")
public interface ProductoClient {

    /**
     * Realiza una petición GET al microservicio de productos para obtener un producto por su ID.
     *
     * @param id ID del producto que se desea obtener.
     * @return ResponseEntity que contiene un objeto ProductoPojo con los datos del producto.
     */
    @GetMapping("/api/v1/productos/{id}")
    ResponseEntity<ProductoPojo> getProductoById(@PathVariable Long id);
}