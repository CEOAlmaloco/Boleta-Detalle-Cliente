package com.ampuero.msvc.detalle.services;

import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.models.entities.Detalle;

import java.util.List;

public interface DetalleService {
    DetalleResponseDTO crearDetalle(DetalleDTO detalleDTO) throws ResourceNotFoundException;
    List<DetalleResponseDTO> obtenerPorBoleta(Long idBoleta);
    List<DetalleResponseDTO> obtenerTodos();
    DetalleResponseDTO actualizarDetalle(Long idDetalle, DetalleDTO detalleDTO) throws DetalleException, ResourceNotFoundException;
    void eliminarDetalle(Long idDetalle) throws ResourceNotFoundException;
}

