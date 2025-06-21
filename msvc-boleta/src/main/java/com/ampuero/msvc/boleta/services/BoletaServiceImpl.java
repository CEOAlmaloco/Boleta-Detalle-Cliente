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

/**
 * Implementación del servicio de gestión de boletas.
 *
 * Esta clase maneja todas las operaciones CRUD relacionadas con boletas,
 * incluyendo la integración con el servicio de clientes mediante Feign Client.
 *
 * @author Perfulandia Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BoletaServiceImpl implements BoletaService {

    private static final Logger log = LoggerFactory.getLogger(BoletaServiceImpl.class);

    private final BoletaRepository boletaRepository;
    private final ClienteClientRest clienteClient;

    /**
     * Obtiene los datos de un cliente por su ID.
     *
     * @param idCliente ID del cliente a buscar
     * @return ClienteResponseDTO con los datos del cliente
     * @throws ResourceNotFoundException si el cliente no existe
     * @throws BoletaException si hay errores de comunicación con el servicio
     */
    private ClienteResponseDTO obtenerClienteOExcepcion(Long idCliente) {
        try {
            ClienteResponseDTO cliente = clienteClient.findClienteById(idCliente);
            if (cliente == null) {
                log.warn("Servicio de clientes devolvio null para ID: {}", idCliente);
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente + " (respuesta nula del servicio)");
            }
            return cliente;
        } catch (FeignException e) {
            log.error("Error Feign al obtener clientes ID {}: {}", idCliente, e.getMessage(), e);
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente, e);
            }
            throw new BoletaException("Error al comunicar con servicio de clientes para ID " + idCliente + ": " + e.getMessage(), e);
        }
    }

    /**
     * Crea una nueva boleta en el sistema.
     *
     * @param boletaDTO datos de la boleta a crear
     * @return BoletaResponseDTO con los datos de la boleta creada
     * @throws ResourceNotFoundException si el cliente asociado no existe
     * @throws BoletaException si hay errores durante la creación
     */
    @Override
    public BoletaResponseDTO crearBoleta(BoletaDTO boletaDTO) {
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boletaDTO.getIdClientePojo());
        // ver si el cleinte es nulo

        Boleta boleta = new Boleta();
        boleta.setDescripcionBoleta(boletaDTO.getDescripcionBoleta());
        boleta.setIdClientePojo(cliente.getIdUsuario()); // Usar el ID del clientes obtenido
        boleta.setTotalBoleta(0.0); // Total inicial en 0

        Boleta savedBoleta = boletaRepository.save(boleta);
        log.info("Boleta creada con ID: {}", savedBoleta.getIdBoleta());
        return buildResponseDTO(savedBoleta, cliente);
    }

    /**
     * Obtiene una boleta específica por su ID.
     *
     * @param id ID de la boleta a consultar
     * @return BoletaResponseDTO con los datos de la boleta
     * @throws ResourceNotFoundException si la boleta o el cliente no existen
     */
    @Override
    public BoletaResponseDTO obtenerBoletaPorId(Long id) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + id));
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo());

        return buildResponseDTO(boleta, cliente);
    }

    /**
     * Obtiene todas las boletas del sistema.
     *
     * Las boletas cuyos clientes no puedan ser consultados serán omitidas
     * del resultado y se registrará un warning en el log.
     *
     * @return Lista de BoletaResponseDTO con todas las boletas válidas
     */
    @Override
    public List<BoletaResponseDTO> obtenerTodas() {
        return boletaRepository.findAll().stream()
                .map(boleta -> {
                    try {
                        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo()); // Cambio aqui, getIdClientePojo()
                        return buildResponseDTO(boleta, cliente);
                    } catch (ResourceNotFoundException | BoletaException e) {
                        log.warn("No se pudo obtener el clientes ({}) para la boleta {}: {}. Se omitira la boleta.",
                                boleta.getIdClientePojo(), boleta.getIdBoleta(), e.getMessage());
                        return null; // grackiasgpt por lograr encontrar el error
                    }
                })
                .filter(dto -> dto != null) // Filtrar los nulos
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el total de una boleta sumando el monto especificado.
     *
     * @param idBoleta ID de la boleta a actualizar
     * @param monto monto a sumar al total actual
     * @throws ResourceNotFoundException si la boleta no existe
     */
    @Override
    public void actualizarTotalBoleta(Long idBoleta, Double monto) {
        Boleta boleta = boletaRepository.findById(idBoleta)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + idBoleta + " para actualizar total"));
        boleta.setTotalBoleta(boleta.getTotalBoleta() + monto);
        boletaRepository.save(boleta);
        log.info("Total de boleta ID {} actualizado con monto {}", idBoleta, monto);
    }

    /**
     * Obtiene todas las boletas asociadas a un cliente específico.
     *
     * @param idCliente ID del cliente
     * @return Lista de BoletaResponseDTO del cliente
     * @throws ResourceNotFoundException si el cliente no existe
     */
    @Override
    public List<BoletaResponseDTO> obtenerPorCliente(Long idCliente) {
        // Primero verificar que el clientes existe y obtener sus datos
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(idCliente);

        return boletaRepository.findByIdClientePojo(idCliente).stream()
                .map(boleta -> buildResponseDTO(boleta, cliente)) // Reutiliza el clientes ya obtenido
                .collect(Collectors.toList());
    }

    /**
     * Elimina una boleta del sistema.
     *
     * @param idBoleta ID de la boleta a eliminar
     * @throws ResourceNotFoundException si la boleta no existe
     */
    @Override
    public void eliminarBoleta(Long idBoleta) {
        if (!boletaRepository.existsById(idBoleta)) {
            throw new ResourceNotFoundException("Boleta con ID " + idBoleta + " no encontrada para eliminar");
        }
        boletaRepository.deleteById(idBoleta);
        log.info("Boleta eliminada con ID: {}", idBoleta);
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