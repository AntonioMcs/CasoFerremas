package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.FacturacionClienteDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.FacturacionCliente;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.FacturacionClienteRepository;

@Service
public class FacturacionClienteService {

    private final FacturacionClienteRepository facturacionClienteRepository;
    private final ClienteRepository clienteRepository;

    public FacturacionClienteService(FacturacionClienteRepository facturacionClienteRepository,
            ClienteRepository clienteRepository) {
        this.facturacionClienteRepository = facturacionClienteRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<FacturacionCliente> listarTodos() {
        return facturacionClienteRepository.findAll();
    }

    public Optional<FacturacionCliente> buscarPorId(Integer id) {
        return facturacionClienteRepository.findById(id);
    }

    public FacturacionCliente crear(FacturacionClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        FacturacionCliente facturacion = new FacturacionCliente(null, cliente, dto.getRut(), dto.getNombre(),
                dto.getApellidos(), dto.getTelefono(), dto.getDireccion(), dto.getComuna());
        return facturacionClienteRepository.save(facturacion);
    }

    public FacturacionCliente actualizar(Integer id, FacturacionClienteDTO dto) {
        FacturacionCliente existing = facturacionClienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facturacion no encontrada"));
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        existing.setCliente(cliente);
        existing.setRut(dto.getRut());
        existing.setNombre(dto.getNombre());
        existing.setApellidos(dto.getApellidos());
        existing.setTelefono(dto.getTelefono());
        existing.setDireccion(dto.getDireccion());
        existing.setComuna(dto.getComuna());
        return facturacionClienteRepository.save(existing);
    }

    public void eliminar(Integer id) {
        facturacionClienteRepository.deleteById(id);
    }
}
