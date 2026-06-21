package com.profecarlos.tallerapirest.restapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.VentaRequestDTO;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.service.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/cliente")
    public ResponseEntity<?> checkoutCliente(@Valid @RequestBody VentaRequestDTO dto) {
        return crearVenta(dto);
    }

    @PostMapping("/vendedor")
    public ResponseEntity<?> ventaVendedor(@Valid @RequestBody VentaRequestDTO dto) {
        return crearVenta(dto);
    }

    private ResponseEntity<?> crearVenta(VentaRequestDTO dto) {
        try {
            Pedido pedido = ventaService.crearVenta(dto);
            return new ResponseEntity<>(pedido, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
