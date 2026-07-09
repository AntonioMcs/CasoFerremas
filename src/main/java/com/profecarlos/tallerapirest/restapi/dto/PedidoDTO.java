package com.profecarlos.tallerapirest.restapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PedidoDTO {

    @NotNull
    private Integer clienteId;

    private Integer trabajadorId;

    @NotNull
    private Integer estadoId;

    private Integer productoId;

    private LocalDateTime fechaPedido;

    private BigDecimal total;

    @NotBlank
    private String metodoPago;

    @NotBlank
    private String tipoEntrega;

<<<<<<< HEAD
    private String direccionEntrega;
    private String comunaEntrega;
    private String sucursalRetiro;
=======
    private String grupoCompraId;

    private Integer pedidoReferencia;
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517

    public PedidoDTO() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(Integer trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
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
=======
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
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
    }
}
