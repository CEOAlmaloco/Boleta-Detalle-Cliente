package com.ampuero.msvc.boleta.services;

import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;

import java.util.List;

public interface BoletaService {
    BoletaResponseDTO crearBoleta(BoletaDTO boletaDTO);
    List<BoletaResponseDTO> obtenerTodas();
    List<BoletaResponseDTO> obtenerPorCliente(Long idCliente);
    void eliminarBoleta(Long idFactura);
    void actualizarTotalBoleta(Long idFactura, Double monto);
    BoletaResponseDTO obtenerBoletaPorId(Long id);
}
