package com.profecarlos.tallerapirest.restapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.LoginRequestDTO;
import com.profecarlos.tallerapirest.restapi.dto.LoginResponseDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;

    public AuthController(ClienteRepository clienteRepository, TrabajadorRepository trabajadorRepository) {
        this.clienteRepository = clienteRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        String email = request.getEmail().trim();
        String contrasena = request.getContrasena();

        return clienteRepository.findByEmail(email)
                .filter(cliente -> passwordMatches(cliente.getContrasena(), contrasena))
                .<ResponseEntity<?>>map(this::clienteResponse)
                .orElseGet(() -> trabajadorRepository.findByEmail(email)
                        .filter(trabajador -> Boolean.TRUE.equals(trabajador.getActivo()))
                        .filter(trabajador -> passwordMatches(trabajador.getContrasena(), contrasena))
                        .<ResponseEntity<?>>map(this::trabajadorResponse)
                        .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Email o contrasena incorrectos.")));
    }

    private boolean passwordMatches(String storedPassword, String receivedPassword) {
        return storedPassword != null && storedPassword.equals(receivedPassword);
    }

    private ResponseEntity<LoginResponseDTO> clienteResponse(Cliente cliente) {
        return ResponseEntity.ok(new LoginResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                "cliente",
                "cliente",
                cliente.getComuna()));
    }

    private ResponseEntity<LoginResponseDTO> trabajadorResponse(Trabajador trabajador) {
        return ResponseEntity.ok(new LoginResponseDTO(
                trabajador.getId(),
                trabajador.getNombre(),
                trabajador.getEmail(),
                trabajador.getRol(),
                "trabajador",
                null));
    }
}
