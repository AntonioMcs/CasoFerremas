package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.PedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@Service
public class PedidoService {

    private static final List<String> METODOS_PAGO = List.of("efectivo", "tarjeta", "transferencia");
    private static final List<String> TIPOS_ENTREGA = List.of("retiro_tienda", "despacho_domicilio");

    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, UserRepository userRepository,
            EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPorUsuario(Integer usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarPorEstado(Integer estadoId) {
        return pedidoRepository.findByEstadoPedidoIdEstado(estadoId);
    }

    public Pedido crear(PedidoDTO dto) {
        validar(dto);
        Usuario usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstadoPedido(estadoPedido);
        pedido.setFechaPedido(dto.getFechaPedido());
        pedido.setTotal(dto.getTotal());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTipoEntrega(dto.getTipoEntrega());
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Integer id, PedidoDTO dto) {
        validar(dto);
        Pedido existing = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Usuario usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado"));

        existing.setUsuario(usuario);
        existing.setEstadoPedido(estadoPedido);
        if (dto.getFechaPedido() != null) {
            existing.setFechaPedido(dto.getFechaPedido());
        }
        existing.setTotal(dto.getTotal());
        existing.setMetodoPago(dto.getMetodoPago());
        existing.setTipoEntrega(dto.getTipoEntrega());
        return pedidoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }

    private void validar(PedidoDTO dto) {
        if (!METODOS_PAGO.contains(dto.getMetodoPago())) {
            throw new IllegalArgumentException("metodo_pago debe ser efectivo, tarjeta o transferencia");
        }
        if (!TIPOS_ENTREGA.contains(dto.getTipoEntrega())) {
            throw new IllegalArgumentException("tipo_entrega debe ser retiro_tienda o despacho_domicilio");
        }
    }
}
