package com.profecarlos.tallerapirest.restapi.dto;

import java.util.List;

import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.service.TransbankTransactionResponse;

public class VentaResponseDTO {

    private Pedido pedido;
    private List<Pedido> pedidos;
    private Integer pedidoPrincipalId;
    private String grupoCompraId;
    private TransbankTransactionResponse transbankResponse;

    public VentaResponseDTO() {
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Integer getPedidoPrincipalId() {
        return pedidoPrincipalId;
    }

    public void setPedidoPrincipalId(Integer pedidoPrincipalId) {
        this.pedidoPrincipalId = pedidoPrincipalId;
    }

    public String getGrupoCompraId() {
        return grupoCompraId;
    }

    public void setGrupoCompraId(String grupoCompraId) {
        this.grupoCompraId = grupoCompraId;
    }

    public TransbankTransactionResponse getTransbankResponse() {
        return transbankResponse;
    }

    public void setTransbankResponse(TransbankTransactionResponse transbankResponse) {
        this.transbankResponse = transbankResponse;
    }
}
