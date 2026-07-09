package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.ProveedorDTO;
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor crear(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreProveedor(dto.getNombreProveedor());
        proveedor.setDatosContacto(dto.getDatosContacto());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Integer id, ProveedorDTO dto) {
        Proveedor existing = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        if (dto.getNombreProveedor() != null && !dto.getNombreProveedor().trim().isEmpty()) {
            existing.setNombreProveedor(dto.getNombreProveedor());
        }
        if (dto.getDatosContacto() != null && !dto.getDatosContacto().trim().isEmpty()) {
            existing.setDatosContacto(dto.getDatosContacto());
        }
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            existing.setTelefono(dto.getTelefono());
        }
        if (dto.getDireccion() != null) {
            existing.setDireccion(dto.getDireccion());
        }
        return proveedorRepository.save(existing);
    }

    public void eliminar(Integer id) {
        proveedorRepository.deleteById(id);
    }
}
