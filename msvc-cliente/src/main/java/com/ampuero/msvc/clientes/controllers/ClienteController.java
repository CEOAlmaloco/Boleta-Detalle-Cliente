package com.ampuero.msvc.clientes.controllers;

import com.ampuero.msvc.clientes.dtos.ClienteCreationDTO;
import com.ampuero.msvc.clientes.models.Cliente;
import com.ampuero.msvc.clientes.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("api/v1/clientes")
@Validated
public class ClienteController {
    @Autowired
    public ClienteService clienteService;

    // POST: Crear nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody ClienteCreationDTO clienteCreationDTO){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.crearCliente(clienteCreationDTO));
    }

    // GET: Traer todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> traerTodos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.traerTodos());
    }

    // GET: Traer cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> traerCliente(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.traerPorId(id));
    }

    // PUT: Actualizar cliente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.actualizarCliente(id, cliente));
    }
    // DELETE: Eliminar cliente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Cliente> eliminarCliente(@PathVariable Long id){
        clienteService.eliminarCliente(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
