package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.InventarioDTO;
import com.profecarlos.tallerapirest.restapi.model.Inventario;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.InventarioRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductRepository productRepository;
    private final ProveedorRepository proveedorRepository;

    public InventarioService(InventarioRepository inventarioRepository, ProductRepository productRepository,
            ProveedorRepository proveedorRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productRepository = productRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> buscarPorId(Integer id) {
        return inventarioRepository.findById(id);
    }

    public Inventario crear(InventarioDTO dto) {
        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        }

        Inventario inventario = new Inventario(null, producto, dto.getStockActual(), dto.getStockMinimo(),
                dto.getUbicacionBodega());
        inventario.setProveedor(proveedor);
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Integer id, InventarioDTO dto) {
        Inventario existing = inventarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

        if (dto.getStockActual() != null) {
            existing.setStockActual(dto.getStockActual());
        }
        if (dto.getStockMinimo() != null) {
            existing.setStockMinimo(dto.getStockMinimo());
        }
        if (dto.getUbicacionBodega() != null) {
            existing.setUbicacionBodega(dto.getUbicacionBodega());
        }
        if (dto.getProductoId() != null) {
            Product producto = productRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            existing.setProducto(producto);
        }
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
            existing.setProveedor(proveedor);
        }
        return inventarioRepository.save(existing);
    }

    public void eliminar(Integer id) {
        inventarioRepository.deleteById(id);
    }
}
