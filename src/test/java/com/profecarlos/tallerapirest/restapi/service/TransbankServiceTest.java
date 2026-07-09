package com.profecarlos.tallerapirest.restapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import cl.transbank.webpay.webpayplus.WebpayPlus;

class TransbankServiceTest {

    @Test
    void shouldCreateFallbackResponseWhenSdkFails() throws Exception {
        WebpayPlus.Transaction transaction = mock(WebpayPlus.Transaction.class);
        when(transaction.create(anyString(), anyString(), anyDouble(), anyString()))
                .thenThrow(new RuntimeException("simulated network failure"));

        TransbankService service = new TransbankService("http://localhost:5173/", transaction);

        TransbankTransactionResponse response = service.crearTransaccion(
                new BigDecimal("15000"),
                "ORD_1",
                "SES_1",
                "http://localhost:5173/"
        );

        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getMessage()).contains("simulada");
        assertThat(response.getUrl()).isEqualTo("http://localhost:5173/");
    }
}
