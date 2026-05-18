# API: Detalles de Pedido

Base path: `/api/v1/detalles-pedido`

## Endpoints

### GET /api/v1/detalles-pedido
- Devuelve todos los detalles de pedido.
- Respuesta: `200 OK`.

### GET /api/v1/detalles-pedido/{id}
- Devuelve un detalle de pedido por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### GET /api/v1/detalles-pedido/pedido/{pedidoId}
- Devuelve todos los detalles de un pedido específico.
- Respuesta: `200 OK` con lista JSON.

### POST /api/v1/detalles-pedido
- Crea un detalle de pedido.
- Body JSON requerido:
```json
{
  "pedidoId": 1,
  "productoId": 2,
  "cantidad": 3,
  "precioUnitario": 49990,
  "subtotal": 149970
}
```
- Si no se envía `precioUnitario`, se toma el precio del producto.
- Si no se envía `subtotal`, se calcula automáticamente.
- Respuesta: `201 Created`.

### PUT /api/v1/detalles-pedido/{id}
- Actualiza un detalle de pedido.
- Body JSON similar al de creación.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/detalles-pedido/{id}
- Elimina un detalle de pedido.
- Respuesta: `204 No Content`.
