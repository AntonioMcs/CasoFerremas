package com.profecarlos.tallerapirest.restapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoletaPedidoDTO {

    private Integer pedidoId;
    private String numeroBoleta;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaBoleta;
    private String clienteNombre;
    private String clienteRut;
    private String clienteEmail;
    private String metodoPago;
    private String estadoPedido;
    private String tipoEntrega;
    private String direccionEntrega;
    private String comunaEntrega;
    private String sucursalRetiro;
    private BigDecimal neto;
    private BigDecimal iva;
    private BigDecimal total;
    private List<BoletaItemDTO> items = new ArrayList<>();

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDateTime getFechaBoleta() {
        return fechaBoleta;
    }

    public void setFechaBoleta(LocalDateTime fechaBoleta) {
        this.fechaBoleta = fechaBoleta;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteRut() {
        return clienteRut;
    }

    public void setClienteRut(String clienteRut) {
        this.clienteRut = clienteRut;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<BoletaItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BoletaItemDTO> items) {
        this.items = items;
    }
}
