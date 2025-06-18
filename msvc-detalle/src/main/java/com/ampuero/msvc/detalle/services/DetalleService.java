package com.ampuero.msvc.detalle.services;

/**
 * DetalleService.java
 *
 * Descripción: Interfaz que define los métodos del servicio para la gestión de los detalles
 * asociados a boletas dentro del microservicio de detalle. Representa la capa de lógica de negocio
 * entre el controlador y el repositorio.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 *
 * Funcionalidad:
 * - Declaración de operaciones principales para crear, actualizar, eliminar y consultar detalles de boleta.
 * - Manejo de excepciones específicas relacionadas con errores de validación o recursos inexistentes.
 * - Utiliza DTOs para encapsular los datos de entrada y salida, favoreciendo una arquitectura desacoplada.
 */

import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.models.entities.Detalle;

import java.util.List;

public interface DetalleService {

    /**
     * Crea un nuevo detalle de boleta a partir del DTO recibido.
     *
     * @param detalleDTO Objeto con los datos necesarios para crear el detalle.
     * @return DetalleResponseDTO con los datos del detalle creado.
     * @throws ResourceNotFoundException si no se encuentra la boleta o producto relacionado.
     */
    DetalleResponseDTO crearDetalle(DetalleDTO detalleDTO) throws ResourceNotFoundException;

    /**
     * Obtiene todos los detalles asociados a una boleta específica.
     *
     * @param idBoleta ID de la boleta.
     * @return Lista de DetalleResponseDTO relacionados con la boleta.
     */

    List<DetalleResponseDTO> obtenerPorBoleta(Long idBoleta);
    /**
     * Obtiene todos los detalles registrados en el sistema.
     *
     * @return Lista de todos los DetalleResponseDTO.
     */

    List<DetalleResponseDTO> obtenerTodos();
    /**
     * Actualiza los datos de un detalle existente.
     *
     * @param idDetalle ID del detalle a actualizar.
     * @param detalleDTO Objeto con los nuevos datos del detalle.
     * @return DetalleResponseDTO actualizado.
     * @throws DetalleException si hay errores de validación o reglas de negocio.
     * @throws ResourceNotFoundException si no se encuentra el detalle a actualizar.
     */

    DetalleResponseDTO actualizarDetalle(Long idDetalle, DetalleDTO detalleDTO) throws DetalleException, ResourceNotFoundException;
    /**
     * Elimina un detalle del sistema por su ID.
     *
     * @param idDetalle ID del detalle a eliminar.
     * @throws ResourceNotFoundException si el detalle no existe.
     */

    void eliminarDetalle(Long idDetalle) throws ResourceNotFoundException;
}

