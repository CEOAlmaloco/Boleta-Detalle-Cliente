package com.ampuero.msvc.producto.repositories;

import com.ampuero.msvc.producto.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
