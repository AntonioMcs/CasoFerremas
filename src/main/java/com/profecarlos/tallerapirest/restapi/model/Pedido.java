package com.profecarlos.tallerapirest.restapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido extends RepresentationModel<Pedido> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({ "links" })
    private Cliente cliente;

    @Column(name = "id_cliente", insertable = false, updatable = false)
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_trabajador")
    @JsonIgnoreProperties({ "links" })
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({ "categoria", "proveedor", "links" })
    private Product producto;

    @Column(name = "id_producto", insertable = false, updatable = false)
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties({ "links" })
    private EstadoPedido estadoPedido;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "tipo_entrega", length = 50)
    private String tipoEntrega;

<<<<<<< HEAD
    @Column(name = "direccion_entrega", length = 255)
    private String direccionEntrega;

    @Column(name = "comuna_entrega", length = 100)
    private String comunaEntrega;

    @Column(name = "sucursal_retiro", length = 100)
    private String sucursalRetiro;

    @Column(name = "numero_boleta", unique = true, length = 50)
    private String numeroBoleta;

    @Column(name = "neto", precision = 10, scale = 2)
    private BigDecimal neto;

    @Column(name = "iva", precision = 10, scale = 2)
    private BigDecimal iva;

    @Column(name = "boleta_emitida")
    private Boolean boletaEmitida = false;

    @Column(name = "fecha_boleta")
    private LocalDateTime fechaBoleta;
=======
    @Column(name = "grupo_compra_id", length = 64)
    private String grupoCompraId;

    @Column(name = "pedido_referencia")
    private Integer pedidoReferencia;

    @Column(name = "id_usuario", insertable = false, updatable = false)
    private Integer idUsuario;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517

    public Pedido() {
    }

    @PrePersist
    public void prePersist() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

<<<<<<< HEAD
    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getComunaEntrega() {
        return comunaEntrega;
    }

    public void setComunaEntrega(String comunaEntrega) {
        this.comunaEntrega = comunaEntrega;
    }

    public String getSucursalRetiro() {
        return sucursalRetiro;
    }

    public void setSucursalRetiro(String sucursalRetiro) {
        this.sucursalRetiro = sucursalRetiro;
    }

    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public BigDecimal getNeto() {
        return neto;
    }

    public void setNeto(BigDecimal neto) {
        this.neto = neto;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public Boolean getBoletaEmitida() {
        return boletaEmitida;
    }

    public void setBoletaEmitida(Boolean boletaEmitida) {
        this.boletaEmitida = boletaEmitida;
    }

    public LocalDateTime getFechaBoleta() {
        return fechaBoleta;
    }

    public void setFechaBoleta(LocalDateTime fechaBoleta) {
        this.fechaBoleta = fechaBoleta;
=======
    public Product getProducto() {
        return producto;
    }

    public void setProducto(Product producto) {
        this.producto = producto;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getGrupoCompraId() {
        return grupoCompraId;
    }

    public void setGrupoCompraId(String grupoCompraId) {
        this.grupoCompraId = grupoCompraId;
    }

    public Integer getPedidoReferencia() {
        return pedidoReferencia;
    }

    public void setPedidoReferencia(Integer pedidoReferencia) {
        this.pedidoReferencia = pedidoReferencia;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
    }
}
