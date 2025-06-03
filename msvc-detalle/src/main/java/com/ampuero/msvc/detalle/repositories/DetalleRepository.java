package com.ampuero.msvc.detalle.repositories;

import com.ampuero.msvc.detalle.models.entities.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long> {
    List<Detalle> findByIdBoletaPojo(Long idBoleta);
}
