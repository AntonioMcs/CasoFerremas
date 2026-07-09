package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.Comparator;
import java.util.List;
=======
import java.util.ArrayList;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
import java.util.Locale;
import java.util.UUID;

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
        validarEntrega(dto);
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Trabajador trabajador = null;
        if (dto.getTrabajadorId() != null) {
            trabajador = trabajadorRepository.findById(dto.getTrabajadorId())
                    .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        }

        String metodoPago = dto.getMetodoPago() == null ? "" : dto.getMetodoPago().trim().toLowerCase(Locale.ROOT);
<<<<<<< HEAD
        String estadoNombre = "tarjeta".equals(metodoPago) || "transferencia".equals(metodoPago) ? "pendiente" : "pagado";
        EstadoPedido estado = estadoPedidoRepository.findByNombreEstado(estadoNombre)
                .orElseGet(() -> estadoPedidoRepository.save(new EstadoPedido(null, estadoNombre)));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTrabajador(trabajador);
        pedido.setEstadoPedido(estado);
        pedido.setMetodoPago(metodoPago);
        pedido.setTipoEntrega(dto.getTipoEntrega());
        pedido.setDireccionEntrega(normalize(dto.getDireccionEntrega()));
        pedido.setComunaEntrega(normalize(dto.getComunaEntrega()));
        pedido.setSucursalRetiro(normalize(dto.getSucursalRetiro()));
        pedido.setTotal(BigDecimal.ZERO);
        Pedido guardado = pedidoRepository.save(pedido);
=======
        String estadoNombre = "efectivo".equals(metodoPago) ? "pagado" : "pendiente";
        EstadoPedido estado = obtenerEstadoPedido(estadoNombre);

        String grupoCompraId = "GRP_" + UUID.randomUUID();
        BigDecimal totalGlobal = BigDecimal.ZERO;
        Integer pedidoReferencia = null;
        Pedido pedidoPrincipal = null;
        ArrayList<Pedido> pedidosGenerados = new ArrayList<>();
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517

        for (VentaItemDTO item : dto.getItems()) {
            Product producto = productRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));
            Inventario inventario = resolverInventario(item);
            if (inventario.getStockActual() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombreProducto());
            }

            if (!"tarjeta".equals(metodoPago)) {
                descontarInventario(inventario, item.getCantidad(), producto.getNombreProducto());
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setTrabajador(trabajador);
            pedido.setEstadoPedido(estado);
            pedido.setMetodoPago(metodoPago);
            pedido.setTipoEntrega(dto.getTipoEntrega());
            pedido.setProducto(producto);
            pedido.setGrupoCompraId(grupoCompraId);
            pedido.setTotal(subtotal);
            Pedido guardado = pedidoRepository.save(pedido);

            if (pedidoReferencia == null) {
                pedidoReferencia = guardado.getIdPedido();
                pedidoPrincipal = guardado;
            }
            guardado.setPedidoReferencia(pedidoReferencia);
            guardado = pedidoRepository.save(guardado);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(guardado);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detalle.setInventarioId(inventario.getIdInventario());
            detalle.setOrigenStock(nombreOrigen(inventario));
            detallePedidoRepository.save(detalle);

            totalGlobal = totalGlobal.add(subtotal);
            pedidosGenerados.add(guardado);
        }

<<<<<<< HEAD
        guardado.setTotal(total);
        actualizarTotalesBoleta(guardado);
        if (!"tarjeta".equals(metodoPago) && !"transferencia".equals(metodoPago)) {
            emitirBoleta(guardado);
        }
        guardado = pedidoRepository.save(guardado);
=======
        if (pedidoPrincipal == null) {
            throw new IllegalArgumentException("No se pudo generar el pedido");
        }

        pedidoPrincipal.setTotal(totalGlobal);
        pedidoPrincipal = pedidoRepository.save(pedidoPrincipal);
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517

        VentaResponseDTO responseDTO = new VentaResponseDTO();
        responseDTO.setPedido(pedidoPrincipal);
        responseDTO.setPedidos(pedidosGenerados);
        responseDTO.setPedidoPrincipalId(pedidoPrincipal.getIdPedido());
        responseDTO.setGrupoCompraId(grupoCompraId);

        Pago pago = new Pago();
        pago.setPedido(pedidoPrincipal);
        pago.setMonto(totalGlobal);
        pago.setMetodoPago("TRANSBANK");
        pago.setEstadoPago("PENDIENTE");
        pago.setFechaPago(LocalDateTime.now());
        pago = pagoRepository.save(pago);

        if ("tarjeta".equals(metodoPago)) {
            String retorno = "http://localhost:5173/transbank-return?pagoId=" + pago.getIdPago();
            try {
                String ordenCompra = "PEDIDO_" + pedidoPrincipal.getIdPedido();
                String sesionId = "SESION_" + pedidoPrincipal.getIdPedido();
                TransbankTransactionResponse transbankResponse = transbankService.crearTransaccion(totalGlobal, ordenCompra, sesionId, retorno);
                String status = transbankResponse.getStatus() != null ? transbankResponse.getStatus().trim().toUpperCase(Locale.ROOT) : "PENDING";
                pago.setMetodoPago("TRANSBANK");
                pago.setTokenTransbank(transbankResponse.getToken());
                pago.setAuthorizationCode(transbankResponse.getAuthorizationCode());

                if ("AUTHORIZED".equals(status)) {
                    pago.setEstadoPago("PAGADO");
                    pago.setFechaPago(LocalDateTime.now());
                    guardado = confirmarPedidoPagado(guardado);
                    pagoRepository.save(pago);
                    responseDTO.setPedido(guardado);
                    transbankResponse.setUrl(null);
                    responseDTO.setTransbankResponse(transbankResponse);
                    return responseDTO;
                }

                pago.setEstadoPago("PENDING".equals(status) ? "PENDIENTE" : "PROCESANDO");
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

<<<<<<< HEAD
    @Transactional
    public VentaResponseDTO confirmarPagoTransbank(Integer pagoId, String token) throws Exception {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        TransbankTransactionResponse estado = transbankService.obtenerEstadoTransaccion(token);
        String status = estado.getStatus() != null ? estado.getStatus().trim().toUpperCase(Locale.ROOT) : "";

        if ("AUTHORIZED".equals(status)) {
            pago.setEstadoPago("PAGADO");
            pago.setMetodoPago("TRANSBANK");
            pago.setTokenTransbank(token);
            pago.setAuthorizationCode(estado.getAuthorizationCode());
            pago.setFechaPago(LocalDateTime.now());
            Pedido pedido = confirmarPedidoPagado(pago.getPedido());
            pagoRepository.save(pago);

            VentaResponseDTO response = new VentaResponseDTO();
            response.setPedido(pedido);
            response.setTransbankResponse(estado);
            return response;
        }

        if ("REVERSED".equals(status) || "FAILED".equals(status) || "NULLIFIED".equals(status)) {
            pago.setEstadoPago("RECHAZADO");
            cambiarEstadoPedido(pago.getPedido(), "rechazado");
            pagoRepository.save(pago);
        }

        VentaResponseDTO response = new VentaResponseDTO();
        response.setPedido(pago.getPedido());
        response.setTransbankResponse(estado);
        return response;
    }

    @Transactional
    public Pedido confirmarPedidoPagado(Pedido pedido) {
        if (Boolean.TRUE.equals(pedido.getBoletaEmitida())) {
            return pedido;
        }

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(pedido.getIdPedido());
        for (DetallePedido detalle : detalles) {
            Inventario inventario = resolverInventarioParaDetalle(detalle);
            descontarInventario(inventario, detalle.getCantidad(), detalle.getProducto().getNombreProducto());
            detalle.setInventarioId(inventario.getIdInventario());
            detalle.setOrigenStock(nombreOrigen(inventario));
            detallePedidoRepository.save(detalle);
        }

        emitirBoleta(pedido);
        cambiarEstadoPedido(pedido, "pagado");
        return pedidoRepository.save(pedido);
=======
    private EstadoPedido obtenerEstadoPedido(String estadoNombre) {
        return estadoPedidoRepository.findAllByNombreEstadoIgnoreCaseOrderByIdEstadoAsc(estadoNombre).stream()
                .findFirst()
                .orElseGet(() -> estadoPedidoRepository.save(new EstadoPedido(null, estadoNombre)));
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
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
                .sorted(Comparator.comparing((Inventario inventario) -> !isWebStock(inventario)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para producto: " + item.getProductoId()));
    }

    private Inventario resolverInventarioParaDetalle(DetallePedido detalle) {
        if (detalle.getInventarioId() != null) {
            return inventarioRepository.findById(detalle.getInventarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado: " + detalle.getInventarioId()));
        }
        return inventarioRepository.findByProductoId(detalle.getProducto().getId()).stream()
                .sorted(Comparator.comparing((Inventario inventario) -> !isWebStock(inventario)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para producto: " + detalle.getProducto().getNombreProducto()));
    }

    private void descontarInventario(Inventario inventario, Integer cantidad, String nombreProducto) {
        if (inventario.getStockActual() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para " + nombreProducto + " en " + nombreOrigen(inventario));
        }
        inventario.setStockActual(inventario.getStockActual() - cantidad);
        inventarioRepository.save(inventario);
    }

    private void validarEntrega(VentaRequestDTO dto) {
        String tipoEntrega = normalize(dto.getTipoEntrega());
        if (!"retiro_tienda".equals(tipoEntrega) && !"despacho_domicilio".equals(tipoEntrega)) {
            throw new IllegalArgumentException("tipoEntrega debe ser retiro_tienda o despacho_domicilio");
        }
        if ("despacho_domicilio".equals(tipoEntrega) && (normalize(dto.getDireccionEntrega()) == null || normalize(dto.getComunaEntrega()) == null)) {
            throw new IllegalArgumentException("Debe indicar direccion y comuna para despacho a domicilio");
        }
        if ("retiro_tienda".equals(tipoEntrega) && normalize(dto.getSucursalRetiro()) == null) {
            throw new IllegalArgumentException("Debe seleccionar sucursal para retiro");
        }
    }

    private void actualizarTotalesBoleta(Pedido pedido) {
        BigDecimal total = pedido.getTotal() != null ? pedido.getTotal() : BigDecimal.ZERO;
        BigDecimal neto = total.divide(BigDecimal.valueOf(1.19), 2, RoundingMode.HALF_UP);
        pedido.setNeto(neto);
        pedido.setIva(total.subtract(neto));
    }

    private void emitirBoleta(Pedido pedido) {
        actualizarTotalesBoleta(pedido);
        pedido.setNumeroBoleta("BOL-" + pedido.getIdPedido() + "-" + System.currentTimeMillis());
        pedido.setFechaBoleta(LocalDateTime.now());
        pedido.setBoletaEmitida(true);
    }

    private void cambiarEstadoPedido(Pedido pedido, String nombreEstado) {
        EstadoPedido estado = estadoPedidoRepository.findByNombreEstadoIgnoreCase(nombreEstado)
                .orElseGet(() -> estadoPedidoRepository.save(new EstadoPedido(null, nombreEstado)));
        pedido.setEstadoPedido(estado);
        pedidoRepository.save(pedido);
    }

    private String nombreOrigen(Inventario inventario) {
        String sucursal = normalize(inventario.getSucursal());
        if (sucursal != null) {
            return isWebStock(inventario) ? "Web" : sucursal;
        }
        String ubicacion = normalize(inventario.getUbicacionBodega());
        return ubicacion != null ? ubicacion : "Web";
    }

    private boolean isWebStock(Inventario inventario) {
        String label = (inventario.getSucursal() != null ? inventario.getSucursal() : inventario.getUbicacionBodega());
        if (label == null) {
            return true;
        }
        String normalized = label.toLowerCase(Locale.ROOT);
        return normalized.contains("web") || normalized.contains("online");
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
