package com.profecarlos.tallerapirest.restapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.PedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private static final List<String> METODOS_PAGO = List.of("efectivo", "tarjeta", "transferencia");
    private static final List<String> TIPOS_ENTREGA = List.of("retiro_tienda", "despacho_domicilio");

    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository, UserRepository userRepository,
            EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Integer id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(pedidoRepository.findByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PedidoDTO dto) {
        ResponseEntity<?> validacion = validarDTO(dto);
        if (validacion != null) {
            return validacion;
        }

        Usuario usuario = userRepository.findById(dto.getUsuarioId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }

        EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId()).orElse(null);
        if (estadoPedido == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de pedido no encontrado");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstadoPedido(estadoPedido);
        pedido.setFechaPedido(dto.getFechaPedido());
        pedido.setTotal(dto.getTotal());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTipoEntrega(dto.getTipoEntrega());

        return new ResponseEntity<>(pedidoRepository.save(pedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody PedidoDTO dto) {
        ResponseEntity<?> validacion = validarDTO(dto);
        if (validacion != null) {
            return validacion;
        }

        return pedidoRepository.findById(id).map(existing -> {
            Usuario usuario = userRepository.findById(dto.getUsuarioId()).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
            }

            EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId()).orElse(null);
            if (estadoPedido == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de pedido no encontrado");
            }

            existing.setUsuario(usuario);
            existing.setEstadoPedido(estadoPedido);
            existing.setFechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : existing.getFechaPedido());
            existing.setTotal(dto.getTotal());
            existing.setMetodoPago(dto.getMetodoPago());
            existing.setTipoEntrega(dto.getTipoEntrega());

            return ResponseEntity.ok(pedidoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarDTO(PedidoDTO dto) {
        if (!METODOS_PAGO.contains(dto.getMetodoPago())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("metodo_pago debe ser efectivo, tarjeta o transferencia");
        }
        if (!TIPOS_ENTREGA.contains(dto.getTipoEntrega())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("tipo_entrega debe ser retiro_tienda o despacho_domicilio");
        }
        return null;
    }
}
