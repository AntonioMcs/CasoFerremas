package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.CarritoCompraDTO;
import com.profecarlos.tallerapirest.restapi.model.CarritoCompra;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.CarritoCompraRepository;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;

@Service
public class CarritoCompraService {

    private final CarritoCompraRepository carritoCompraRepository;
    private final ClienteRepository clienteRepository;
    private final ProductRepository productRepository;

    public CarritoCompraService(CarritoCompraRepository carritoCompraRepository, ClienteRepository clienteRepository,
            ProductRepository productRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.clienteRepository = clienteRepository;
        this.productRepository = productRepository;
    }

    public List<CarritoCompra> listarTodos() {
        return carritoCompraRepository.findAll();
    }

    public Optional<CarritoCompra> buscarPorId(Integer id) {
        return carritoCompraRepository.findById(id);
    }

    public List<CarritoCompra> listarPorCliente(Integer clienteId) {
        return carritoCompraRepository.findByClienteId(clienteId);
    }

    public CarritoCompra crear(CarritoCompraDTO dto) {
        CarritoCompra carritoCompra = new CarritoCompra();
        aplicarDatos(carritoCompra, dto);
        return carritoCompraRepository.save(carritoCompra);
    }

    public CarritoCompra actualizar(Integer id, CarritoCompraDTO dto) {
        CarritoCompra existing = carritoCompraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));
        aplicarDatos(existing, dto);
        return carritoCompraRepository.save(existing);
    }

    public void eliminar(Integer id) {
        carritoCompraRepository.deleteById(id);
    }

    private void aplicarDatos(CarritoCompra carritoCompra, CarritoCompraDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;
        BigDecimal precioUnitario = dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio();

        carritoCompra.setCliente(cliente);
        carritoCompra.setProducto(producto);
        carritoCompra.setCantidad(dto.getCantidad());
        carritoCompra.setPrecioUnitario(precioUnitario);
        carritoCompra.setDescuento(descuento);
        carritoCompra.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : precioUnitario.multiply(BigDecimal.valueOf(dto.getCantidad())).subtract(descuento));
        if (dto.getFechaAgregado() != null) {
            carritoCompra.setFechaAgregado(dto.getFechaAgregado());
        }
    }
}
