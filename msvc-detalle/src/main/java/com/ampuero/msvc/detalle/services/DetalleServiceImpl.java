package com.ampuero.msvc.detalle.services;

import com.ampuero.msvc.detalle.dtos.DetalleDTO;
import com.ampuero.msvc.detalle.dtos.DetalleResponseDTO;
import com.ampuero.msvc.detalle.exceptions.DetalleException;
import com.ampuero.msvc.detalle.exceptions.ResourceNotFoundException;
import com.ampuero.msvc.detalle.models.BoletaPojo;
import com.ampuero.msvc.detalle.models.ProductoPojo;
import com.ampuero.msvc.detalle.models.entities.Detalle;
import com.ampuero.msvc.detalle.repositories.DetalleRepository;
import com.ampuero.msvc.detalle.clients.BoletaClient;
import com.ampuero.msvc.detalle.clients.ProductoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import feign.FeignException;
import com.ampuero.msvc.detalle.models.ClientePojo;
import com.ampuero.msvc.detalle.dtos.ClienteEnBoletaDTO;
import com.ampuero.msvc.detalle.dtos.BoletaEnDetalleDTO;
import com.ampuero.msvc.detalle.dtos.MontoUpdateRequestDTO;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DetalleServiceImpl implements DetalleService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DetalleServiceImpl.class);

    private final DetalleRepository detalleRepository;
    private final ProductoClient productoClient;
    private final BoletaClient boletaClient;

    // Crea un nuevo detalle y Lanza excepcion si la boleta o producto no existen.
    @Override
    public DetalleResponseDTO crearDetalle(DetalleDTO detalleDTO) throws ResourceNotFoundException, DetalleException {
        BoletaPojo boleta = obtenerBoletaOExcepcion(detalleDTO.getIdBoletaPojo());
        ProductoPojo producto = obtenerProductoOExcepcion(detalleDTO.getIdProductoPojo());

        Detalle detalle = new Detalle();
        detalle.setIdBoletaPojo(boleta.getIdBoleta());
        detalle.setIdProductoPojo(producto.getIdProducto());
        detalle.setCantidadDetalle(detalleDTO.getCantidadDetalle());
        detalle.setPrecioUnitarioDetalle(producto.getPrecioProducto());
        detalle.setSubtotalDetalle(detalleDTO.getCantidadDetalle() * producto.getPrecioProducto());

        Detalle detalleGuardado = detalleRepository.save(detalle);
        actualizarTotalBoleta(boleta.getIdBoleta(), detalleGuardado.getSubtotalDetalle());

        return construirResponse(detalleGuardado, boleta, producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleResponseDTO> obtenerPorBoleta(Long idBoleta) {
        // Obtiene detalles por ID de boleta. Omite detalles si la boleta o producto asociados no se encuentran.
        return detalleRepository.findByIdBoletaPojo(idBoleta).stream()
                .map(detalle -> {
                    BoletaPojo boleta = null;
                    ProductoPojo producto = null;
                    try {
                        boleta = obtenerBoletaOExcepcion(detalle.getIdBoletaPojo());
                        producto = obtenerProductoOExcepcion(detalle.getIdProductoPojo());
                        return construirResponse(detalle, boleta, producto);
                    } catch (ResourceNotFoundException | DetalleException e) {
                        log.warn("No se pudo obtener la boleta ({}) o el producto ({}) para el detalle {}: {}. Se omitirá el detalle en la respuesta.", 
                                 detalle.getIdBoletaPojo(), detalle.getIdProductoPojo(), detalle.getIdDetalle(), e.getMessage());
                        return null; // Omitir este detalle si las dependencias no se pueden cargar
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleResponseDTO> obtenerTodos() {
        // Obtiene todos los detalles. Omite detalles si la boleta o producto asociados no se encuentran.
        return detalleRepository.findAll().stream()
                .map(detalle -> {
                    BoletaPojo boleta = null;
                    ProductoPojo producto = null;
                    try {
                        boleta = obtenerBoletaOExcepcion(detalle.getIdBoletaPojo());
                        producto = obtenerProductoOExcepcion(detalle.getIdProductoPojo());
                        return construirResponse(detalle, boleta, producto);
                    } catch (ResourceNotFoundException | DetalleException e) {
                        log.warn("No se pudo obtener la boleta ({}) o el producto ({}) para el detalle {}: {}. Se omitirá el detalle en la respuesta.", 
                                 detalle.getIdBoletaPojo(), detalle.getIdProductoPojo(), detalle.getIdDetalle(), e.getMessage());
                        return null; //  el log warn se lo sAQue a gpt gracias profe por dejarnos usar ia sino llevaria dias con el error de que no se pudo obtener la boleta
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // Actualiza un detalle existente. Lanza excepcion si el detalle, la nueva boleta o el nuevo producto no existen.
    @Override
    public DetalleResponseDTO actualizarDetalle(Long idDetalle, DetalleDTO detalleDTO) throws ResourceNotFoundException, DetalleException {
        // Busca detalle existente o lanza DetalleException si no se encuentra
        Detalle detalleExistente = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new DetalleException("Detalle no encontrado con ID: " + idDetalle));

        double subtotalAnterior = detalleExistente.getSubtotalDetalle();
        Long idBoletaOriginal = detalleExistente.getIdBoletaPojo();

        BoletaPojo nuevaBoleta = obtenerBoletaOExcepcion(detalleDTO.getIdBoletaPojo());
        ProductoPojo nuevoProducto = obtenerProductoOExcepcion(detalleDTO.getIdProductoPojo());

        detalleExistente.setIdBoletaPojo(nuevaBoleta.getIdBoleta());
        detalleExistente.setIdProductoPojo(nuevoProducto.getIdProducto());
        detalleExistente.setCantidadDetalle(detalleDTO.getCantidadDetalle());
        detalleExistente.setPrecioUnitarioDetalle(nuevoProducto.getPrecioProducto());
        double nuevoSubtotal = detalleDTO.getCantidadDetalle() * nuevoProducto.getPrecioProducto();
        detalleExistente.setSubtotalDetalle(nuevoSubtotal);

        Detalle detalleActualizado = detalleRepository.save(detalleExistente);

        if (!idBoletaOriginal.equals(nuevaBoleta.getIdBoleta())) {
            actualizarTotalBoleta(idBoletaOriginal, -subtotalAnterior);
            actualizarTotalBoleta(nuevaBoleta.getIdBoleta(), nuevoSubtotal);
        } else {
            actualizarTotalBoleta(nuevaBoleta.getIdBoleta(), nuevoSubtotal - subtotalAnterior);
        }

        return construirResponse(detalleActualizado, nuevaBoleta, nuevoProducto);
    }

    // Elimina un detalle. Lanza ResourceNotFoundException si el detalle no existe.
    @Override
    public void eliminarDetalle(Long idDetalle) throws ResourceNotFoundException {
        // Busca detalle o lanza ResourceNotFoundException si no se encuentra
        Detalle detalle = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + idDetalle));

        actualizarTotalBoleta(detalle.getIdBoletaPojo(), -detalle.getSubtotalDetalle());
        detalleRepository.delete(detalle);
    }

    // Llama al clientes de boletas para actualizar el total
    private void actualizarTotalBoleta(Long idBoleta, double monto) {
        MontoUpdateRequestDTO montoDTO = new MontoUpdateRequestDTO(monto);
        boletaClient.actualizarTotalBoleta(idBoleta, montoDTO);
    }

    // Construye el DTO de respuesta combinando Detalle, BoletaPojo y ProductoPojo
    private DetalleResponseDTO construirResponse(Detalle detalle, BoletaPojo boletaPojo, ProductoPojo producto) {
        DetalleResponseDTO response = new DetalleResponseDTO();
        response.setIdDetalle(detalle.getIdDetalle());

        // Mapear BoletaPojo a BoletaEnDetalleDTO
        if (boletaPojo != null) {
            ClienteEnBoletaDTO clienteEnBoletaDTO = null;
            if (boletaPojo.getCliente() != null) {
                ClientePojo clientePojo = boletaPojo.getCliente();
                clienteEnBoletaDTO = new ClienteEnBoletaDTO(
                        clientePojo.getIdUsuario(),
                        clientePojo.getNombreCliente(),
                        clientePojo.getCorreoCliente()
                );
            }

            BoletaEnDetalleDTO boletaEnDetalleDTO = new BoletaEnDetalleDTO(
                    boletaPojo.getIdBoleta(), // Esto se mapeará a idFactura en el DTO
                    boletaPojo.getFechaEmisionBoleta(),
                    boletaPojo.getTotalBoleta(),
                    boletaPojo.getDescripcionBoleta(),
                    clienteEnBoletaDTO
            );
            response.setBoleta(boletaEnDetalleDTO);
        } else {
            response.setBoleta(null); // O manejar como error si la boleta es indispensable
        }

        response.setProducto(producto);
        response.setCantidadDetalle(detalle.getCantidadDetalle());
        response.setPrecioUnitarioDetalle(detalle.getPrecioUnitarioDetalle());
        response.setSubtotalDetalle(detalle.getSubtotalDetalle());
        return response;
    }

    // Obtiene BoletaPojo del servicio de boletas y Lanza excepcion si no se encuentra o hay error.
    private BoletaPojo obtenerBoletaOExcepcion(Long idBoleta) throws ResourceNotFoundException {
        try {
            ResponseEntity<BoletaPojo> boletaResponse = boletaClient.getBoletaById(idBoleta);
            // Verifica si la respuesta del clientes Feign es un error
            if (boletaResponse.getStatusCode().isError()) {
                String errorMsg = "Error del servicio de boletas al obtener ID " + idBoleta +": " + boletaResponse.getStatusCode();
                log.warn(errorMsg);
                throw new ResourceNotFoundException(errorMsg);
            }
            BoletaPojo boleta = boletaResponse.getBody();
            if (boleta == null || boleta.getIdBoleta() == null) {
                // Respuesta inesperada del servicio de boletas
                String errorMsg = "Respuesta nula o boleta sin ID del servicio de boletas para ID: " + idBoleta;
                log.warn(errorMsg);
                throw new ResourceNotFoundException(errorMsg);
            }
            return boleta;
        } catch (FeignException e) {
            String errorMsg = "Error al comunicar con servicio de boletas para ID " + idBoleta + ": " + e.getMessage();
            log.warn(errorMsg, e); // Loguear la causa raíz gracias gpt
            if (e.status() == 404) {
                // Boleta no encontrada especificamente
                throw new ResourceNotFoundException("Boleta no encontrada con ID: " + idBoleta, e);
            }
            // Otro error de Feign, se envia a DetalleException
            throw new DetalleException(errorMsg, e);
        }
    }

    // Obtiene ProductoPojo del servicio de productos. Lanza excepcion si no se encuentra o hay error.
    private ProductoPojo obtenerProductoOExcepcion(Long idProducto) throws ResourceNotFoundException {
        try {
            ResponseEntity<ProductoPojo> productoResponse = productoClient.getProductoById(idProducto);
            // Verifica si la respuesta del clientes Feign es un error
            if (productoResponse.getStatusCode().isError()) {
                String errorMsg = "Error del servicio de productos al obtener ID " + idProducto + ": " + productoResponse.getStatusCode();
                log.warn(errorMsg);
                throw new ResourceNotFoundException(errorMsg);
            }
            ProductoPojo producto = productoResponse.getBody();
            if (producto == null || producto.getIdProducto() == null) {
                // Respuesta inesperada del servicio de productos
                 String errorMsg = "Respuesta nula o producto sin ID del servicio de productos para ID: " + idProducto;
                log.warn(errorMsg);
                throw new ResourceNotFoundException(errorMsg);
            }
            return producto;
        } catch (FeignException e) {
            String errorMsg = "Error al comunicar con servicio de productos para ID " + idProducto + ": " + e.getMessage();
            log.warn(errorMsg, e); // Loguear la causa raíz
            if (e.status() == 404) {
                // Producto no encontrado especificamente
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto, e);
            }
            // Otro error de Feign, se envia a DetalleException
            throw new DetalleException(errorMsg, e);
        }
    }
}