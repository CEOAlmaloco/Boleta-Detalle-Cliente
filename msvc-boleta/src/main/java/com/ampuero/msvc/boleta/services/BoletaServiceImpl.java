package com.ampuero.msvc.boleta.services;

import com.ampuero.msvc.boleta.clients.ClienteClientRest;
import com.ampuero.msvc.boleta.dtos.BoletaDTO;
import com.ampuero.msvc.boleta.dtos.BoletaResponseDTO;
import com.ampuero.msvc.boleta.dtos.ClienteResponseDTO;
import com.ampuero.msvc.boleta.exceptions.BoletaException;
import com.ampuero.msvc.boleta.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.boleta.models.entities.Boleta;
import com.ampuero.msvc.boleta.repositories.BoletaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoletaServiceImpl implements BoletaService {

    private static final Logger log = LoggerFactory.getLogger(BoletaServiceImpl.class);

    private final BoletaRepository boletaRepository;
    private final ClienteClientRest clienteClient;

    // Metodo privado para obtener cliente o lanzar excepcion
    private ClienteResponseDTO obtenerClienteOExcepcion(Long idCliente) {
        try {
            ClienteResponseDTO cliente = clienteClient.findClienteById(idCliente);
            if (cliente == null) {
                log.warn("Servicio de clientes devolvio null para ID: {}", idCliente);
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente + " (respuesta nula del servicio)");
            }
            return cliente;
        } catch (FeignException e) {
            log.error("Error Feign al obtener cliente ID {}: {}", idCliente, e.getMessage(), e);
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente, e);
            }
            throw new BoletaException("Error al comunicar con servicio de clientes para ID " + idCliente + ": " + e.getMessage(), e);
        }
    }

    // POST: Crear nueva boleta. Lanza excepcion si el cliente no existe.
    @Override
    public BoletaResponseDTO crearBoleta(BoletaDTO boletaDTO) {
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boletaDTO.getIdClientePojo());
        // ver si el cleinte es nulo

        Boleta boleta = new Boleta();
        boleta.setDescripcionBoleta(boletaDTO.getDescripcionBoleta());
        boleta.setIdClientePojo(cliente.getIdUsuario()); // Usar el ID del cliente obtenido
        boleta.setTotalBoleta(0.0); // Total inicial en 0

        Boleta savedBoleta = boletaRepository.save(boleta);
        log.info("Boleta creada con ID: {}", savedBoleta.getIdBoleta());
        return buildResponseDTO(savedBoleta, cliente);
    }

    // GET: Obtener boleta por ID. Lanza excepcion si la boleta o el cliente asociado no existen.
    @Override
    public BoletaResponseDTO obtenerBoletaPorId(Long id) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + id));
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo());

        return buildResponseDTO(boleta, cliente);
    }

    // GET: Obtener todas las boletas Omite boletas si el cliente asociado no se encuentra.
    @Override
    public List<BoletaResponseDTO> obtenerTodas() {
        return boletaRepository.findAll().stream()
                .map(boleta -> {
                    try {
                        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo()); // Cambio aqui, getIdClientePojo()
                        return buildResponseDTO(boleta, cliente);
                    } catch (ResourceNotFoundException | BoletaException e) {
                        log.warn("No se pudo obtener el cliente ({}) para la boleta {}: {}. Se omitira la boleta.",
                                 boleta.getIdClientePojo(), boleta.getIdBoleta(), e.getMessage());
                        return null; // grackiasgpt por lograr encontrar el error
                    }
                })
                .filter(dto -> dto != null) // Filtrar los nulos
                .collect(Collectors.toList());
    }

    // PUT: Actualizar total de una boleta. Lanza excepcion si la boleta no existe.
    @Override
    public void actualizarTotalBoleta(Long idFactura, Double monto) {
        Boleta boleta = boletaRepository.findById(idFactura)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + idFactura + " para actualizar total"));
        boleta.setTotalBoleta(boleta.getTotalBoleta() + monto);
        boletaRepository.save(boleta);
        log.info("Total de boleta ID {} actualizado con monto {}", idFactura, monto);
    }

    // GET: Obtener boletas por ID de cliente. Lanza excepcion si el cliente no existe.
    @Override
    public List<BoletaResponseDTO> obtenerPorCliente(Long idCliente) {
        // Primero verificar que el cliente existe y obtener sus datos
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(idCliente);

        return boletaRepository.findByIdClientePojo(idCliente).stream()
                .map(boleta -> buildResponseDTO(boleta, cliente)) // Reutiliza el cliente ya obtenido
                .collect(Collectors.toList());
    }

    // DELETE: Eliminar boleta por ID. Lanza excepcion si la boleta no existe.
    @Override
    public void eliminarBoleta(Long idFactura) {
        if (!boletaRepository.existsById(idFactura)) {
            throw new ResourceNotFoundException("Boleta con ID " + idFactura + " no encontrada para eliminar");
        }
        boletaRepository.deleteById(idFactura);
        log.info("Boleta eliminada con ID: {}", idFactura);
    }

    // Construye el DTO de respuesta para la boleta
    private BoletaResponseDTO buildResponseDTO(Boleta boleta, ClienteResponseDTO cliente) {
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setIdBoleta(boleta.getIdBoleta());
        response.setFechaEmisionBoleta(boleta.getFechaEmisionBoleta());
        response.setTotalBoleta(boleta.getTotalBoleta());
        response.setDescripcionBoleta(boleta.getDescripcionBoleta());
        response.setCliente(cliente);
        return response;
    }
}