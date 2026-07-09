# API: Pagos y Transbank

Base path: `/api/v1/pagos`

## Endpoints

### GET /api/v1/pagos
- Devuelve todos los pagos.
- Respuesta: `200 OK`.

### GET /api/v1/pagos/{id}
- Devuelve un pago por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### GET /api/v1/pagos/pedido/{pedidoId}
- Devuelve el pago asociado a un pedido.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/pagos
- Crea un pago en estado pendiente.
- Body JSON requerido:
```json
{
  "pedidoId": 1,
  "monto": 249990,
  "metodoPago": "PENDIENTE",
  "estadoPago": "PENDIENTE",
  "fechaPago": "2026-05-17T10:00:00"
}
```
- Respuesta: `201 Created` con mensaje de éxito.

### POST /api/v1/pagos/{pagoId}/transbank?retorno={URL_DE_RETORNO}
- Inicia la transacción en Transbank.
- Parámetros:
  - `pagoId`: ID del pago.
  - `retorno`: URL a la que Transbank redirige después del pago.
- Respuesta: `201 Created` con objeto JSON que incluye:
  - `status`
  - `transactionId` (token)
  - `authorizationCode` (URL de pago Transbank)
  - `message`
- El pago pasa a estado `PROCESANDO` y `metodoPago` se marca como `TRANSBANK`.

### GET /api/v1/pagos/{pagoId}/transbank/estado/{token}
- Consulta el estado de una transacción Transbank.
- Parámetros:
  - `pagoId`: ID del pago.
  - `token`: token devuelto por Transbank.
- Respuesta:
  - `AUTHORIZED`: pago completado y estado guardado como `COMPLETADO`.
  - `REVERSED`: pago rechazado y estado guardado como `RECHAZADO`.
  - Otros estados: pendiente.

### PUT /api/v1/pagos/{id}
- Actualiza un pago.
- Body JSON opcional similar al de creación.
- No permite actualizar pagos con estado `COMPLETADO`.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/pagos/{id}
- Elimina un pago.
- No permite eliminar pagos `COMPLETADO`.
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.

## Uso de Transbank

1. Crear el pago con `POST /api/v1/pagos`.
2. Generar la transacción Transbank con:
   `POST /api/v1/pagos/{pagoId}/transbank?retorno=https://miapp.com/retorno`
3. Obtener estado con:
   `GET /api/v1/pagos/{pagoId}/transbank/estado/{token}`

### Notas
- `retorno` es obligatorio.
- El token Transbank devuelto se usa para consultar el estado.
- Las credenciales actuales son de prueba, debes reemplazarlas para producción.
