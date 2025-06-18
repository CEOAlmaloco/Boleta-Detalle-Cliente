package com.ampuero.msvc.detalle.clients;

/**
 * BoletaClient.java
 *
 * Descripción:
 * Interfaz Feign que permite la comunicación declarativa entre el microservicio de detalle
 * y el microservicio de boletas (`msvc-boletas`). Se utiliza para obtener información de
 * boletas y actualizar el total asociado a una boleta cuando se agregan o eliminan detalles.
 *
 * Funcionalidad:
 * - Obtener una boleta específica por su ID.
 * - Actualizar el monto total de una boleta en base a la suma o resta de subtotales de detalles.
 *
 * Configuración:
 * - Se especifica el nombre lógico del servicio como `msvc-boletas` y la URL base local.
 * - En un entorno con Eureka, el parámetro `url` puede ser omitido y resuelto por descubrimiento.
 *
 * Uso típico:
 * - Al crear un nuevo detalle, se suma el subtotal al total de la boleta.
 * - Al eliminar o modificar un detalle, se ajusta el total de la boleta correspondiente.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [Indicar fecha]
 * Última modificación: [Indicar fecha]
 */

import com.ampuero.msvc.detalle.models.BoletaPojo;
import com.ampuero.msvc.detalle.dtos.MontoUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-boletas", url = "http://localhost:8081")
public interface BoletaClient {

    /**
     * Obtiene una boleta específica desde el microservicio de boletas, a partir de su ID.
     *
     * @param id ID único de la boleta que se desea recuperar.
     * @return ResponseEntity con el objeto BoletaPojo, que contiene los datos de la boleta.
     */
    @GetMapping("/api/v1/boletas/{id}")
    ResponseEntity<BoletaPojo> getBoletaById(@PathVariable Long id);

    /**
     * Actualiza el total acumulado de una boleta, sumando o restando un monto determinado.
     * Este mét0do se invoca al crear, modificar o eliminar un detalle.
     *
     * @param idBoleta ID de la boleta a actualizar.
     * @param montoDTO Objeto que contiene el monto a sumar (positivo) o restar (negativo).
     */
    @PutMapping("/api/v1/boletas/{idBoleta}/total")
    void actualizarTotalBoleta(
            @PathVariable("idBoleta") Long idBoleta,
            @RequestBody MontoUpdateRequestDTO montoDTO);
}