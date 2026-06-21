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
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private static final List<String> METODOS_PAGO = List.of("efectivo", "tarjeta", "transferencia");
    private static final List<String> TIPOS_ENTREGA = List.of("retiro_tienda", "despacho_domicilio");

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            TrabajadorRepository trabajadorRepository, EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.trabajadorRepository = trabajadorRepository;
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

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(pedidoRepository.findByClienteId(clienteId));
    }

    @GetMapping("/pendientes-transferencia")
    public ResponseEntity<List<Pedido>> listarTransferenciasPendientes() {
        return ResponseEntity.ok(pedidoRepository.findByMetodoPagoAndEstadoPedidoNombreEstadoIgnoreCase("transferencia", "pendiente"));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PedidoDTO dto) {
        ResponseEntity<?> validacion = validarDTO(dto);
        if (validacion != null) {
            return validacion;
        }

        Pedido pedido = new Pedido();
        ResponseEntity<?> resultado = aplicarRelaciones(pedido, dto);
        if (resultado != null) {
            return resultado;
        }
        aplicarDatos(pedido, dto);
        return new ResponseEntity<>(pedidoRepository.save(pedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody PedidoDTO dto) {
        ResponseEntity<?> validacion = validarDTO(dto);
        if (validacion != null) {
            return validacion;
        }

        return pedidoRepository.findById(id).map(existing -> {
            ResponseEntity<?> resultado = aplicarRelaciones(existing, dto);
            if (resultado != null) {
                return resultado;
            }
            aplicarDatos(existing, dto);
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

    private ResponseEntity<?> aplicarRelaciones(Pedido pedido, PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no encontrado");
        }

        EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId()).orElse(null);
        if (estadoPedido == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de pedido no encontrado");
        }

        Trabajador trabajador = null;
        if (dto.getTrabajadorId() != null) {
            trabajador = trabajadorRepository.findById(dto.getTrabajadorId()).orElse(null);
            if (trabajador == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Trabajador no encontrado");
            }
        }

        pedido.setCliente(cliente);
        pedido.setTrabajador(trabajador);
        pedido.setEstadoPedido(estadoPedido);
        return null;
    }

    private void aplicarDatos(Pedido pedido, PedidoDTO dto) {
        pedido.setFechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : pedido.getFechaPedido());
        pedido.setTotal(dto.getTotal());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTipoEntrega(dto.getTipoEntrega());
    }

    private ResponseEntity<?> validarDTO(PedidoDTO dto) {
        if (!METODOS_PAGO.contains(dto.getMetodoPago())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("metodo_pago debe ser efectivo, tarjeta o transferencia");
        }
        if (!TIPOS_ENTREGA.contains(dto.getTipoEntrega())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tipo_entrega debe ser retiro_tienda o despacho_domicilio");
        }
        return null;
    }
}
