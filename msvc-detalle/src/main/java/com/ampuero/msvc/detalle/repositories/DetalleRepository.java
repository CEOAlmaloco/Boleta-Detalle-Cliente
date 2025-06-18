package com.ampuero.msvc.detalle.repositories;

/**
 * DetalleRepository.java
 *
 * Descripción: Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones
 * de persistencia sobre la entidad Detalle dentro del microservicio de detalle.
 *
 * Autor: Alex Ignacio Ampuero Ahumada
 * Fecha de creación: [NN]
 * Última modificación: [17-06-25]
 *
 * Funcionalidad:
 * - Permite realizar operaciones CRUD básicas sobre la entidad Detalle.
 * - Incluye un mét.odo personalizado para obtener todos los detalles asociados a una boleta específica,
 *   utilizando el ID de dicha boleta.
 * - Es parte de la capa de acceso a datos en la arquitectura del microservicio.
 */

import com.ampuero.msvc.detalle.models.entities.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Detalle. Define operaciones de base de datos
 * usando la interfaz JpaRepository de Spring Data JPA.
 */

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long> {

    /**
     * Obtiene una lista de detalles de boleta filtrados por el ID de la boleta.
     *
     * @param idBoleta ID de la boleta a la cual están asociados los detalles.
     * @return Lista de objetos Detalle relacionados con la boleta indicada.
     */
    List<Detalle> findByIdBoletaPojo(Long idBoleta);
}
