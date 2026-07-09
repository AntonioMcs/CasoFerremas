# API: Estados de Pedido

Base path: `/api/v1/estados-pedido`

## Endpoints

### GET /api/v1/estados-pedido
- Devuelve todos los estados de pedido.
- Respuesta: `200 OK`.

### GET /api/v1/estados-pedido/{id}
- Devuelve un estado de pedido específico.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/estados-pedido
- Crea un nuevo estado de pedido.
- Body JSON requerido:
```json
{
  "nombreEstado": "EN_PROCESO"
}
```
- Respuesta: `201 Created`.

### PUT /api/v1/estados-pedido/{id}
- Actualiza un estado de pedido.
- Body JSON requerido:
```json
{
  "nombreEstado": "ENVIADO"
}
```
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/estados-pedido/{id}
- Elimina un estado de pedido.
- Respuesta: `204 No Content`.
