package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.DetallePedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.DetallePedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.DetallePedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository, PedidoRepository pedidoRepository,
            ProductRepository productRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    public List<DetallePedido> listarTodos() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    public List<DetallePedido> listarPorPedido(Integer pedidoId) {
        return detallePedidoRepository.findByPedidoIdPedido(pedidoId);
    }

    public DetallePedido crear(DetallePedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(dto.getCantidad());
        detallePedido.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        detallePedido.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : detallePedido.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));
        return detallePedidoRepository.save(detallePedido);
    }

    public DetallePedido actualizar(Integer id, DetallePedidoDTO dto) {
        DetallePedido existing = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de pedido no encontrado"));

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        Product producto = productRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        existing.setPedido(pedido);
        existing.setProducto(producto);
        existing.setCantidad(dto.getCantidad());
        existing.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        existing.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : existing.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));
        return detallePedidoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        detallePedidoRepository.deleteById(id);
    }
}
