# API: Pedidos

Base path: `/api/v1/pedidos`

## Endpoints

### GET /api/v1/pedidos
- Devuelve todos los pedidos.
- Respuesta: `200 OK`.

### GET /api/v1/pedidos/{id}
- Devuelve un pedido específico.
- Respuesta: `200 OK` o `404 Not Found`.

### GET /api/v1/pedidos/usuario/{usuarioId}
- Devuelve pedidos por usuario.
- Respuesta: `200 OK` con lista JSON.

### POST /api/v1/pedidos
- Crea un pedido.
- Body JSON requerido:
```json
{
  "usuarioId": 1,
  "estadoId": 1,
  "fechaPedido": "2026-05-17T10:00:00",
  "total": 299990,
  "metodoPago": "tarjeta",
  "tipoEntrega": "despacho_domicilio"
}
```
- `metodoPago` debe ser uno de: `efectivo`, `tarjeta`, `transferencia`.
- `tipoEntrega` debe ser uno de: `retiro_tienda`, `despacho_domicilio`.
- Respuesta: `201 Created` con el pedido creado.

### PUT /api/v1/pedidos/{id}
- Actualiza un pedido.
- Body JSON similar al de creación.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/pedidos/{id}
- Elimina un pedido.
- Respuesta: `204 No Content`.
