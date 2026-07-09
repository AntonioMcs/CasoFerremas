package com.profecarlos.tallerapirest.restapi.controller;

import java.util.List;
import java.util.Map;

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

import com.profecarlos.tallerapirest.restapi.dto.BoletaItemDTO;
import com.profecarlos.tallerapirest.restapi.dto.BoletaPedidoDTO;
import com.profecarlos.tallerapirest.restapi.dto.PedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.DetallePedido;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.DetallePedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PagoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;
import com.profecarlos.tallerapirest.restapi.service.AuditLogService;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private static final List<String> METODOS_PAGO = List.of("efectivo", "tarjeta", "transferencia");
    private static final List<String> TIPOS_ENTREGA = List.of("retiro_tienda", "despacho_domicilio");

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
<<<<<<< HEAD
    private final DetallePedidoRepository detallePedidoRepository;
=======
    private final ProductRepository productRepository;
    private final PagoRepository pagoRepository;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
    private final AuditLogService auditLogService;

    public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            TrabajadorRepository trabajadorRepository, EstadoPedidoRepository estadoPedidoRepository,
<<<<<<< HEAD
            DetallePedidoRepository detallePedidoRepository,
            AuditLogService auditLogService) {
=======
            ProductRepository productRepository, PagoRepository pagoRepository, AuditLogService auditLogService) {
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
<<<<<<< HEAD
        this.detallePedidoRepository = detallePedidoRepository;
=======
        this.productRepository = productRepository;
        this.pagoRepository = pagoRepository;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
        this.auditLogService = auditLogService;
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

    @GetMapping("/estado/{nombreEstado}")
    public ResponseEntity<List<Pedido>> listarPorEstado(@PathVariable String nombreEstado) {
        return ResponseEntity.ok(pedidoRepository.findByEstadoPedidoNombreEstadoIgnoreCase(nombreEstado));
    }

    @GetMapping("/bodega")
    public ResponseEntity<List<BoletaPedidoDTO>> listarPedidosBodega() {
        List<String> estados = List.of("pagado", "pendiente", "listo", "preparando", "entregando");
        List<BoletaPedidoDTO> pedidos = pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getEstadoPedido() != null)
                .filter(pedido -> estados.contains(pedido.getEstadoPedido().getNombreEstado().toLowerCase()))
                .map(this::toBoletaDTO)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}/boleta")
    public ResponseEntity<?> obtenerBoleta(@PathVariable Integer id) {
        return pedidoRepository.findById(id)
                .<ResponseEntity<?>>map(pedido -> ResponseEntity.ok(toBoletaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestBody Object estadoRequest) {
        return pedidoRepository.findById(id).map(pedido -> {
            String estadoNombre = extraerNombreEstado(estadoRequest);
            EstadoPedido estado = obtenerEstadoPedido(estadoNombre);
            List<Pedido> pedidosGrupo = pedido.getGrupoCompraId() == null || pedido.getGrupoCompraId().isBlank()
                    ? List.of(pedido)
                    : pedidoRepository.findByGrupoCompraIdOrderByIdPedidoAsc(pedido.getGrupoCompraId());

            Pedido principal = pedido;
            for (Pedido item : pedidosGrupo) {
                item.setEstadoPedido(estado);
                Pedido guardado = pedidoRepository.save(item);
                if (guardado.getIdPedido().equals(item.getPedidoReferencia())) {
                    principal = guardado;
                }
                sincronizarPagoTransferencia(guardado, estadoNombre);
            }

            auditLogService.registrar("PEDIDO", principal.getIdPedido(), "ESTADO", "Estado actualizado a " + estado.getNombreEstado());
            return ResponseEntity.ok(principal);
        }).orElse(ResponseEntity.notFound().build());
    }

    private String extraerNombreEstado(Object estadoRequest) {
        String estadoNombre = null;

        if (estadoRequest instanceof String raw) {
            estadoNombre = raw;
        } else if (estadoRequest instanceof Map<?, ?> body) {
            Object value = body.get("estado");
            if (value == null) {
                value = body.get("nombreEstado");
            }
            estadoNombre = value == null ? null : value.toString();
        }

        if (estadoNombre == null || estadoNombre.trim().isBlank()) {
            throw new IllegalArgumentException("El estado del pedido es requerido");
        }

        return estadoNombre.trim().replace("\"", "").toLowerCase();
    }

    private EstadoPedido obtenerEstadoPedido(String estadoNombre) {
        List<EstadoPedido> estados = estadoPedidoRepository.findAllByNombreEstadoIgnoreCaseOrderByIdEstadoAsc(estadoNombre);
        if (!estados.isEmpty()) {
            return estados.get(0);
        }
        return estadoPedidoRepository.save(new EstadoPedido(null, estadoNombre));
    }

    private void sincronizarPagoTransferencia(Pedido pedido, String estadoNombre) {
        if (pedido.getMetodoPago() == null || !"transferencia".equalsIgnoreCase(pedido.getMetodoPago())) {
            return;
        }

        pagoRepository.findAllByPedidoIdPedidoOrderByIdPagoAsc(pedido.getIdPedido()).forEach(pago -> {
            if ("pagado".equalsIgnoreCase(estadoNombre)) {
                pago.setEstadoPago("PAGADO");
            } else if ("pendiente".equalsIgnoreCase(estadoNombre)) {
                pago.setEstadoPago("PENDIENTE");
            } else {
                return;
            }
            pagoRepository.save(pago);
        });
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
        Pedido guardado = pedidoRepository.save(pedido);
        auditLogService.registrar("PEDIDO", guardado.getIdPedido(), "CREAR", "Pedido creado", guardado.getIdPedido(), null, null);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
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
            Pedido actualizado = pedidoRepository.save(existing);
            auditLogService.registrar("PEDIDO", actualizado.getIdPedido(), "ACTUALIZAR", "Pedido actualizado", actualizado.getIdPedido(), null, null);
            return ResponseEntity.ok(actualizado);
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

        Product producto = null;
        if (dto.getProductoId() != null) {
            producto = productRepository.findById(dto.getProductoId()).orElse(null);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
            }
        }
        pedido.setProducto(producto);

        return null;
    }

    private void aplicarDatos(Pedido pedido, PedidoDTO dto) {
        pedido.setFechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : pedido.getFechaPedido());
        pedido.setTotal(dto.getTotal());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTipoEntrega(dto.getTipoEntrega());
<<<<<<< HEAD
        pedido.setDireccionEntrega(dto.getDireccionEntrega());
        pedido.setComunaEntrega(dto.getComunaEntrega());
        pedido.setSucursalRetiro(dto.getSucursalRetiro());
=======
        pedido.setGrupoCompraId(dto.getGrupoCompraId());
        pedido.setPedidoReferencia(dto.getPedidoReferencia());
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
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

    private BoletaPedidoDTO toBoletaDTO(Pedido pedido) {
        BoletaPedidoDTO dto = new BoletaPedidoDTO();
        dto.setPedidoId(pedido.getIdPedido());
        dto.setNumeroBoleta(pedido.getNumeroBoleta());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setFechaBoleta(pedido.getFechaBoleta());
        dto.setMetodoPago(pedido.getMetodoPago());
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombreEstado() : null);
        dto.setTipoEntrega(pedido.getTipoEntrega());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setComunaEntrega(pedido.getComunaEntrega());
        dto.setSucursalRetiro(pedido.getSucursalRetiro());
        dto.setNeto(pedido.getNeto());
        dto.setIva(pedido.getIva());
        dto.setTotal(pedido.getTotal());

        Cliente cliente = pedido.getCliente();
        if (cliente != null) {
            dto.setClienteNombre(cliente.getNombre());
            dto.setClienteRut(cliente.getRut());
            dto.setClienteEmail(cliente.getEmail());
        }

        List<BoletaItemDTO> items = detallePedidoRepository.findByPedidoIdPedido(pedido.getIdPedido()).stream()
                .map(this::toBoletaItemDTO)
                .toList();
        dto.setItems(items);
        return dto;
    }

    private BoletaItemDTO toBoletaItemDTO(DetallePedido detalle) {
        BoletaItemDTO dto = new BoletaItemDTO();
        Product producto = detalle.getProducto();
        if (producto != null) {
            dto.setProductoId(producto.getId());
            dto.setNombreProducto(producto.getNombreProducto());
            dto.setSku(producto.getCodigoSku());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setInventarioId(detalle.getInventarioId());
        dto.setOrigenStock(detalle.getOrigenStock());
        return dto;
    }
}
