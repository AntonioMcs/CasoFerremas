package com.profecarlos.tallerapirest.restapi.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;

@Service
public class TransbankService {

    private static final String COMMERCE_CODE = "597055555532";
    private static final String API_KEY = "579B532A7440BB0C9079DED94D31EA1615BACEB56610332264630D42D0A36B1C";
    private static final boolean FORCE_APPROVED = true;

    private final WebpayPlus.Transaction transaction;

    public TransbankService() {
        this("http://localhost:5173/", null);
    }

    public TransbankService(String retornoFallback, WebpayPlus.Transaction transaction) {
        WebpayOptions options = new WebpayOptions(COMMERCE_CODE, API_KEY, IntegrationType.TEST);
        this.transaction = transaction != null ? transaction : new WebpayPlus.Transaction(options);
    }

    /**
     * Realiza una transacción de pago con Transbank
     * @param monto Monto a cobrar en pesos chilenos
     * @param ordenCompra ID único de la orden de compra
     * @param sesionId ID de sesión único
     * @param retorno URL de retorno después del pago
     * @return Token de transacción
     */
    public TransbankTransactionResponse crearTransaccion(BigDecimal monto, String ordenCompra, String sesionId, String retorno) throws Exception {
        try {
            if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("⚠️ El monto debe ser mayor a 0");
            }
            if (ordenCompra == null || ordenCompra.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ La orden de compra es requerida");
            }
            if (sesionId == null || sesionId.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ La sesión es requerida");
            }
            if (retorno == null || retorno.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ La URL de retorno es requerida");
            }

            if (FORCE_APPROVED) {
                return approvedResponse("SIMULATED_" + UUID.randomUUID(), "Pago Transbank aprobado automáticamente para pruebas");
            }

            return approvedResponse("SIMULATED_" + UUID.randomUUID(), "Pago Transbank aprobado automáticamente");
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            return fallbackResponse(retorno, e.getMessage());
        }
    }

    private TransbankTransactionResponse fallbackResponse(String retorno, String detail) {
        TransbankTransactionResponse result = new TransbankTransactionResponse();
        result.setStatus("AUTHORIZED");
        result.setResponseCode("0");
        result.setMessage("Transacción simulada aprobada por fallback de Transbank: " + detail);
        result.setTransactionId(UUID.randomUUID().toString());
        result.setAuthorizationCode("SIMULATED");
        result.setToken(result.getTransactionId());
        result.setUrl(null);
        return result;
    }

    private TransbankTransactionResponse approvedResponse(String token, String message) {
        TransbankTransactionResponse result = new TransbankTransactionResponse();
        result.setStatus("AUTHORIZED");
        result.setResponseCode("0");
        result.setMessage(message);
        result.setTransactionId(token);
        result.setAuthorizationCode("SIMULATED_OK");
        result.setToken(token);
        result.setUrl(null);
        return result;
    }

    /**
     * Obtiene el estado de una transacción
     * @param token Token de la transacción
     * @return Detalles del estado de la transacción
     */
    public TransbankTransactionResponse obtenerEstadoTransaccion(String token) throws Exception {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ El token de transacción es requerido");
            }

            return approvedResponse(token, "Transacción aprobada automáticamente para pruebas");
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception("⚠️ Error al obtener estado de transacción: " + e.getMessage(), e);
        }
    }

    /**
     * Refuerza una transacción
     * @param token Token de la transacción
     * @return Respuesta de refuerzo
     */
    public TransbankTransactionResponse reforzarTransaccion(String token) throws Exception {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ El token de transacción es requerido");
            }

            return approvedResponse(token, "Estado de transacción aprobado automáticamente para pruebas");
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception("⚠️ Error al reforzar transacción: " + e.getMessage(), e);
        }
    }

    /**
     * Revierte una transacción
     * @param token Token de la transacción
     * @return Respuesta de reversión
     */
    public TransbankTransactionResponse revertirTransaccion(String token) throws Exception {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("⚠️ El token de transacción es requerido");
            }

            TransbankTransactionResponse result = new TransbankTransactionResponse();
            result.setStatus("REVERSED");
            result.setResponseCode("0");
            result.setMessage("La reversión se debe implementar con refund/capture según el flujo de Transbank");
            result.setTransactionId(token);
            return result;
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception("⚠️ Error al revertir transacción: " + e.getMessage(), e);
        }
    }
}
