package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.ProductDTO;
import com.profecarlos.tallerapirest.restapi.model.Categoria;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.CategoriaRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductService(ProductRepository productRepository, CategoriaRepository categoriaRepository,
            ProveedorRepository proveedorRepository) {
        this.productRepository = productRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    public Optional<Product> buscarPorId(Integer id) {
        return productRepository.findById(id);
    }

    public List<Product> buscarPorNombre(String nombreProducto) {
        return productRepository.findByNombreProductoContainingIgnoreCase(nombreProducto);
    }

    public List<Product> buscarPorCategoria(Categoria categoria) {
        return productRepository.findByCategoria(categoria);
    }

    public Product crear(ProductDTO dto) {
        Product product = new Product(null, dto.getNombreProducto(), dto.getMarca(), dto.getDescripcion(),
                dto.getPrecio(), dto.getUnidadMedida());
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setCodigoSku(dto.getCodigoSku());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            product.setCategoria(categoria);
        }

        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
            product.setProveedor(proveedor);
        }

        return productRepository.save(product);
    }

    public Product actualizar(Integer id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (dto.getNombreProducto() != null && !dto.getNombreProducto().trim().isEmpty()) {
            existing.setNombreProducto(dto.getNombreProducto());
        }
        if (dto.getMarca() != null) {
            existing.setMarca(dto.getMarca());
        }
        if (dto.getDescripcion() != null) {
            existing.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrecio() != null) {
            existing.setPrecio(dto.getPrecio());
        }
        if (dto.getStock() != null) {
            existing.setStock(dto.getStock());
        }
        if (dto.getUnidadMedida() != null) {
            existing.setUnidadMedida(dto.getUnidadMedida());
        }
        if (dto.getCodigoSku() != null) {
            existing.setCodigoSku(dto.getCodigoSku());
        }
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            existing.setCategoria(categoria);
        }
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
            existing.setProveedor(proveedor);
        }

        return productRepository.save(existing);
    }

    public void eliminar(Integer id) {
        productRepository.deleteById(id);
    }
}
