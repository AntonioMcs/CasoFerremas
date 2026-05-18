# API: Carrito de Compras

Base path: `/api/v1/carritos-compras`

## Endpoints

### GET /api/v1/carritos-compras
- Devuelve todos los elementos del carrito.
- Respuesta: `200 OK`.

### GET /api/v1/carritos-compras/{id}
- Devuelve un elemento del carrito por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/carritos-compras
- Crea un elemento en el carrito.
- Body JSON requerido:
```json
{
  "usuarioId": 1,
  "productoId": 2,
  "cantidad": 2,
  "precioUnitario": 49990,
  "descuento": 0,
  "subtotal": 99980,
  "fechaAgregado": "2026-05-17T10:00:00"
}
```
- `precioUnitario` y `descuento` son opcionales.
- Si no se envía `subtotal`, se calcula a partir del precio y cantidad.
- Respuesta: `201 Created`.

### PUT /api/v1/carritos-compras/{id}
- Actualiza un elemento del carrito.
- Body JSON similar al de creación.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/carritos-compras/{id}
- Elimina un elemento del carrito.
- Respuesta: `204 No Content`.
