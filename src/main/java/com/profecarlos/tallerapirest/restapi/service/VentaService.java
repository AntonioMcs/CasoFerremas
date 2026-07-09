package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.profecarlos.tallerapirest.restapi.dto.VentaItemDTO;
import com.profecarlos.tallerapirest.restapi.dto.VentaRequestDTO;
import com.profecarlos.tallerapirest.restapi.dto.VentaResponseDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.DetallePedido;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Inventario;
import com.profecarlos.tallerapirest.restapi.model.Pago;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.DetallePedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.InventarioRepository;
import com.profecarlos.tallerapirest.restapi.repository.PagoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;

@Service
public class VentaService {

    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ProductRepository productRepository;
    private final InventarioRepository inventarioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final TransbankService transbankService;

    public VentaService(ClienteRepository clienteRepository, TrabajadorRepository trabajadorRepository,
            ProductRepository productRepository, InventarioRepository inventarioRepository,
            EstadoPedidoRepository estadoPedidoRepository, PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository, PagoRepository pagoRepository,
            TransbankService transbankService) {
        this.clienteRepository = clienteRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.productRepository = productRepository;
        this.inventarioRepository = inventarioRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.pagoRepository = pagoRepository;
        this.transbankService = transbankService;
    }

    @Transactional
    public VentaResponseDTO crearVenta(VentaRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Trabajador trabajador = null;
        if (dto.getTrabajadorId() != null) {
            trabajador = trabajadorRepository.findById(dto.getTrabajadorId())
                    .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        }

        String metodoPago = dto.getMetodoPago() == null ? "" : dto.getMetodoPago().trim().toLowerCase(Locale.ROOT);
        String estadoNombre = "efectivo".equals(metodoPago) ? "pagado" : "pendiente";
        EstadoPedido estado = obtenerEstadoPedido(estadoNombre);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTrabajador(trabajador);
        pedido.setEstadoPedido(estado);
        pedido.setMetodoPago(metodoPago);
        pedido.setTipoEntrega(dto.getTipoEntrega());
        pedido.setTotal(BigDecimal.ZERO);
        Pedido guardado = pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;
        for (VentaItemDTO item : dto.getItems()) {
            Product producto = productRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));
            Inventario inventario = resolverInventario(item);
            if (inventario.getStockActual() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombreProducto());
            }

            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(guardado);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detallePedidoRepository.save(detalle);
            total = total.add(subtotal);
        }

        guardado.setTotal(total);
        guardado = pedidoRepository.save(guardado);

        VentaResponseDTO responseDTO = new VentaResponseDTO();
        responseDTO.setPedido(guardado);

        Pago pago = new Pago();
        pago.setPedido(guardado);
        pago.setMonto(total);
        pago.setMetodoPago("TRANSBANK");
        pago.setEstadoPago("PENDIENTE");
        pago.setFechaPago(LocalDateTime.now());
        pago = pagoRepository.save(pago);

        if ("tarjeta".equals(metodoPago)) {
            String retorno = "http://localhost:5173/transbank-return?pagoId=" + pago.getIdPago();
            try {
                String ordenCompra = "PEDIDO_" + guardado.getIdPedido();
                String sesionId = "SESION_" + guardado.getIdPedido();
                TransbankTransactionResponse transbankResponse = transbankService.crearTransaccion(total, ordenCompra, sesionId, retorno);
                String status = transbankResponse.getStatus() != null ? transbankResponse.getStatus().trim().toUpperCase(Locale.ROOT) : "PENDING";
                pago.setEstadoPago("PENDING".equals(status) ? "PENDIENTE" : "PROCESANDO");
                pago.setMetodoPago("TRANSBANK");
                pago.setTokenTransbank(transbankResponse.getToken());
                pago.setAuthorizationCode(transbankResponse.getAuthorizationCode());
                String url = transbankResponse.getUrl();
                if (url == null || url.isBlank()) {
                    url = retorno + "&token=" + transbankResponse.getToken();
                    transbankResponse.setUrl(url);
                }
                pago.setUrlTransbank(url);
                pagoRepository.save(pago);
                responseDTO.setTransbankResponse(transbankResponse);
                return responseDTO;
            } catch (Exception e) {
                pago.setEstadoPago("PENDIENTE");
                pagoRepository.save(pago);
                TransbankTransactionResponse fallback = new TransbankTransactionResponse();
                fallback.setStatus("PENDING");
                fallback.setResponseCode("0");
                fallback.setMessage("Error de Transbank: " + e.getMessage());
                fallback.setToken("SIMULATED_" + System.currentTimeMillis());
                fallback.setUrl(retorno + "&token=" + fallback.getToken());
                pago.setTokenTransbank(fallback.getToken());
                pago.setUrlTransbank(fallback.getUrl());
                pagoRepository.save(pago);
                responseDTO.setTransbankResponse(fallback);
                return responseDTO;
            }
        } else {
            pago.setEstadoPago("transferencia".equals(metodoPago) ? "PENDIENTE" : "PAGADO");
            pagoRepository.save(pago);
            responseDTO.setTransbankResponse(null);
            return responseDTO;
        }
    }

    private EstadoPedido obtenerEstadoPedido(String estadoNombre) {
        return estadoPedidoRepository.findAllByNombreEstadoIgnoreCaseOrderByIdEstadoAsc(estadoNombre).stream()
                .findFirst()
                .orElseGet(() -> estadoPedidoRepository.save(new EstadoPedido(null, estadoNombre)));
    }

    private Inventario resolverInventario(VentaItemDTO item) {
        if (item.getInventarioId() != null) {
            return inventarioRepository.findById(item.getInventarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado: " + item.getInventarioId()));
        }
        if (item.getSucursal() != null && !item.getSucursal().isBlank()) {
            return inventarioRepository.findFirstByProductoIdAndSucursalIgnoreCase(item.getProductoId(), item.getSucursal())
                    .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para sucursal: " + item.getSucursal()));
        }
        return inventarioRepository.findByProductoId(item.getProductoId()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para producto: " + item.getProductoId()));
    }
}
