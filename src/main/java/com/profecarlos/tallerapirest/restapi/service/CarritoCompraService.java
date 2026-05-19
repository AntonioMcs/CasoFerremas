package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.CarritoCompraDTO;
import com.profecarlos.tallerapirest.restapi.model.CarritoCompra;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.CarritoCompraRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@Service
public class CarritoCompraService {

    private final CarritoCompraRepository carritoCompraRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CarritoCompraService(CarritoCompraRepository carritoCompraRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<CarritoCompra> listarTodos() {
        return carritoCompraRepository.findAll();
    }

    public Optional<CarritoCompra> buscarPorId(Integer id) {
        return carritoCompraRepository.findById(id);
    }

    public List<CarritoCompra> listarPorUsuario(Integer usuarioId) {
        return carritoCompraRepository.findByUsuarioId(usuarioId);
    }

    public CarritoCompra crear(CarritoCompraDTO dto) {
        Usuario usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        CarritoCompra carritoCompra = new CarritoCompra();
        carritoCompra.setUsuario(usuario);
        carritoCompra.setProducto(producto);
        carritoCompra.setCantidad(dto.getCantidad());
        carritoCompra.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        carritoCompra.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        carritoCompra.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : carritoCompra.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad()))
                        .subtract(carritoCompra.getDescuento()));
        carritoCompra.setFechaAgregado(dto.getFechaAgregado());
        return carritoCompraRepository.save(carritoCompra);
    }

    public CarritoCompra actualizar(Integer id, CarritoCompraDTO dto) {
        CarritoCompra existing = carritoCompraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));

        Usuario usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        existing.setUsuario(usuario);
        existing.setProducto(producto);
        existing.setCantidad(dto.getCantidad());
        existing.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        existing.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        existing.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : existing.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad()))
                        .subtract(existing.getDescuento()));
        if (dto.getFechaAgregado() != null) {
            existing.setFechaAgregado(dto.getFechaAgregado());
        }
        return carritoCompraRepository.save(existing);
    }

    public void eliminar(Integer id) {
        carritoCompraRepository.deleteById(id);
    }
}
