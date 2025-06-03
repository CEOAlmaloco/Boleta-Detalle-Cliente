package com.ampuero.msvc.boleta.repositories;

import com.ampuero.msvc.boleta.models.entities.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// repositories/BoletaRepository.java
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByIdClientePojo(Long idClientePojo);
}