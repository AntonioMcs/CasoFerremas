# API: Despachos

Base path: `/api/v1/despachos`

## Endpoints

### GET /api/v1/despachos
- Devuelve todos los despachos.
- Respuesta: `200 OK`.

### GET /api/v1/despachos/{id}
- Devuelve un despacho por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### GET /api/v1/despachos/pedido/{pedidoId}
- Devuelve el despacho asociado a un pedido.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/despachos
- Crea un despacho.
- Body JSON requerido:
```json
{
  "pedidoId": 1,
  "direccionEntrega": "Av. Siempre Viva 123",
  "fechaEnvio": "2026-05-20T09:00:00",
  "fechaEntrega": "2026-05-22T12:00:00",
  "estadoDespacho": "EN_CAMINO"
}
```
- Respuesta: `201 Created`.

### PUT /api/v1/despachos/{id}
- Actualiza un despacho.
- Body JSON similar al de creación.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/despachos/{id}
- Elimina un despacho.
- Respuesta: `204 No Content`.
