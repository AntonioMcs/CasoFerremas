package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.FacturacionClienteDTO;
import com.profecarlos.tallerapirest.restapi.model.FacturacionCliente;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.FacturacionClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@Service
public class FacturacionClienteService {

    private final FacturacionClienteRepository facturacionClienteRepository;
    private final UserRepository userRepository;

    public FacturacionClienteService(FacturacionClienteRepository facturacionClienteRepository, UserRepository userRepository) {
        this.facturacionClienteRepository = facturacionClienteRepository;
        this.userRepository = userRepository;
    }

    public List<FacturacionCliente> listarTodos() {
        return facturacionClienteRepository.findAll();
    }

    public Optional<FacturacionCliente> buscarPorId(Integer id) {
        return facturacionClienteRepository.findById(id);
    }

    public FacturacionCliente crear(FacturacionClienteDTO dto) {
        Usuario usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        FacturacionCliente facturacion = new FacturacionCliente(null, usuario, dto.getRut(), dto.getNombre(),
                dto.getApellidos(), dto.getTelefono(), dto.getDireccion(), dto.getComuna());
        return facturacionClienteRepository.save(facturacion);
    }

    public FacturacionCliente actualizar(Integer id, FacturacionClienteDTO dto) {
        FacturacionCliente existing = facturacionClienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facturación no encontrada"));

        existing.setRut(dto.getRut());
        existing.setNombre(dto.getNombre());
        existing.setApellidos(dto.getApellidos());
        existing.setTelefono(dto.getTelefono());
        existing.setDireccion(dto.getDireccion());
        existing.setComuna(dto.getComuna());
        if (dto.getUsuarioId() != null) {
            Usuario usuario = userRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            existing.setUsuario(usuario);
        }
        return facturacionClienteRepository.save(existing);
    }

    public void eliminar(Integer id) {
        facturacionClienteRepository.deleteById(id);
    }
}
