package com.profecarlos.tallerapirest.restapi.dto;

import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.service.TransbankTransactionResponse;

public class VentaResponseDTO {

    private Pedido pedido;
    private TransbankTransactionResponse transbankResponse;

    public VentaResponseDTO() {
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public TransbankTransactionResponse getTransbankResponse() {
        return transbankResponse;
    }

    public void setTransbankResponse(TransbankTransactionResponse transbankResponse) {
        this.transbankResponse = transbankResponse;
    }
}
