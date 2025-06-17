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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de boletas.
 *
 * Esta clase maneja todas las operaciones CRUD relacionadas con boletas,
 * incluyendo la integración con el servicio de clientes mediante Feign Client.
 *
 * @author ampuero
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoletaServiceImpl implements BoletaService {

    private final BoletaRepository boletaRepository;
    private final ClienteClientRest clienteClient;

    private static final BigDecimal TOTAL_INICIAL = BigDecimal.ZERO;
    private static final String CLIENTE_NO_ENCONTRADO_MSG = "Cliente no encontrado con ID: ";
    private static final String BOLETA_NO_ENCONTRADA_MSG = "Boleta no encontrada con ID: ";

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
            log.debug("Consultando cliente con ID: {}", idCliente);

            ClienteResponseDTO cliente = clienteClient.findClienteById(idCliente);

            if (Objects.isNull(cliente)) {
                log.warn("El servicio de clientes devolvió null para ID: {}", idCliente);
                throw new ResourceNotFoundException(
                        CLIENTE_NO_ENCONTRADO_MSG + idCliente + " (respuesta nula del servicio)"
                );
            }

            log.debug("Cliente encontrado exitosamente: {}", cliente.getIdUsuario());
            return cliente;

        } catch (FeignException e) {
            log.error("Error Feign al consultar cliente ID {}: {} - Status: {}",
                    idCliente, e.getMessage(), e.status(), e);

            if (e.status() == 404) {
                throw new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO_MSG + idCliente, e);
            }

            throw new BoletaException(
                    String.format("Error al comunicar con servicio de clientes para ID %d: %s",
                            idCliente, e.getMessage()),
                    e
            );
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
    @Transactional
    public BoletaResponseDTO crearBoleta(BoletaDTO boletaDTO) {
        log.info("Iniciando creación de boleta para cliente ID: {}", boletaDTO.getIdClientePojo());

        // Validar que el cliente existe antes de crear la boleta
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boletaDTO.getIdClientePojo());

        // Construir y guardar la nueva boleta
        Boleta nuevaBoleta = Boleta.builder()
                .descripcionBoleta(boletaDTO.getDescripcionBoleta())
                .idClientePojo(cliente.getIdUsuario())
                .totalBoleta(TOTAL_INICIAL.doubleValue())
                .build();

        Boleta boletaGuardada = boletaRepository.save(nuevaBoleta);

        log.info("Boleta creada exitosamente con ID: {} para cliente: {}",
                boletaGuardada.getIdBoleta(), cliente.getIdUsuario());

        return construirResponseDTO(boletaGuardada, cliente);
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
        log.debug("Consultando boleta con ID: {}", id);

        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Boleta no encontrada con ID: {}", id);
                    return new ResourceNotFoundException(BOLETA_NO_ENCONTRADA_MSG + id);
                });

        ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo());

        log.debug("Boleta obtenida exitosamente: {}", id);
        return construirResponseDTO(boleta, cliente);
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
        log.info("Consultando todas las boletas del sistema");

        List<Boleta> todasLasBoletas = boletaRepository.findAll();
        log.debug("Se encontraron {} boletas en la base de datos", todasLasBoletas.size());

        List<BoletaResponseDTO> boletasValidas = todasLasBoletas.stream()
                .map(this::mapearBoletaConManejorDeErrores)
                .filter(Objects::nonNull)
                .toList();

        log.info("Se procesaron exitosamente {} boletas de {} encontradas",
                boletasValidas.size(), todasLasBoletas.size());

        return boletasValidas;
    }

    /**
     * Mapea una boleta a DTO manejando posibles errores de cliente.
     *
     * @param boleta boleta a mapear
     * @return BoletaResponseDTO o null si hay errores
     */
    private BoletaResponseDTO mapearBoletaConManejorDeErrores(Boleta boleta) {
        try {
            ClienteResponseDTO cliente = obtenerClienteOExcepcion(boleta.getIdClientePojo());
            return construirResponseDTO(boleta, cliente);
        } catch (ResourceNotFoundException | BoletaException e) {
            log.warn("No se pudo procesar la boleta ID {} (cliente ID {}): {}. Se omitirá del resultado.",
                    boleta.getIdBoleta(), boleta.getIdClientePojo(), e.getMessage());
            return null;
        }
    }

    /**
     * Actualiza el total de una boleta sumando el monto especificado.
     *
     * @param idBoleta ID de la boleta a actualizar
     * @param monto monto a sumar al total actual
     * @throws ResourceNotFoundException si la boleta no existe
     */
    @Override
    @Transactional
    public void actualizarTotalBoleta(Long idBoleta, Double monto) {
        log.info("Actualizando total de boleta ID {} con monto: {}", idBoleta, monto);

        Boleta boleta = boletaRepository.findById(idBoleta)
                .orElseThrow(() -> {
                    log.warn("Intento de actualizar boleta inexistente con ID: {}", idBoleta);
                    return new ResourceNotFoundException(
                            BOLETA_NO_ENCONTRADA_MSG + idBoleta + " para actualizar total"
                    );
                });

        Double nuevoTotal = boleta.getTotalBoleta() + monto;
        boleta.setTotalBoleta(nuevoTotal);

        boletaRepository.save(boleta);

        log.info("Total de boleta ID {} actualizado exitosamente. Nuevo total: {}",
                idBoleta, nuevoTotal);
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
        log.info("Consultando boletas del cliente ID: {}", idCliente);

        // Validar existencia del cliente primero
        ClienteResponseDTO cliente = obtenerClienteOExcepcion(idCliente);

        List<Boleta> boletasDelCliente = boletaRepository.findByIdClientePojo(idCliente);

        List<BoletaResponseDTO> resultado = boletasDelCliente.stream()
                .map(boleta -> construirResponseDTO(boleta, cliente))
                .toList();

        log.info("Se encontraron {} boletas para el cliente ID: {}",
                resultado.size(), idCliente);

        return resultado;
    }

    /**
     * Elimina una boleta del sistema.
     *
     * @param idBoleta ID de la boleta a eliminar
     * @throws ResourceNotFoundException si la boleta no existe
     */
    @Override
    @Transactional
    public void eliminarBoleta(Long idBoleta) {
        log.info("Eliminando boleta con ID: {}", idBoleta);

        if (!boletaRepository.existsById(idBoleta)) {
            log.warn("Intento de eliminar boleta inexistente con ID: {}", idBoleta);
            throw new ResourceNotFoundException(
                    "Boleta con ID " + idBoleta + " no encontrada para eliminar"
            );
        }

        boletaRepository.deleteById(idBoleta);
        log.info("Boleta eliminada exitosamente con ID: {}", idBoleta);
    }

    /**
     * Construye un DTO de respuesta combinando datos de boleta y cliente.
     *
     * @param boleta entidad boleta
     * @param cliente datos del cliente
     * @return BoletaResponseDTO completo
     */
    private BoletaResponseDTO construirResponseDTO(Boleta boleta, ClienteResponseDTO cliente) {
        return BoletaResponseDTO.builder()
                .idBoleta(boleta.getIdBoleta())
                .fechaEmisionBoleta(boleta.getFechaEmisionBoleta())
                .totalBoleta(boleta.getTotalBoleta())
                .descripcionBoleta(boleta.getDescripcionBoleta())
                .cliente(cliente)
                .build();
    }
}