# API: Facturación de Clientes

Base path: `/api/v1/facturacion-clientes`

## Endpoints

### GET /api/v1/facturacion-clientes
- Devuelve todos los registros de facturación.
- Respuesta: `200 OK`.

### GET /api/v1/facturacion-clientes/{id}
- Devuelve un registro de facturación por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/facturacion-clientes
- Crea una facturación de cliente.
- Body JSON requerido:
```json
{
  "usuarioId": 1,
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellidos": "Pérez",
  "telefono": "912345678",
  "direccion": "Calle 123",
  "comuna": "Santiago"
}
```
- Respuesta: `201 Created`.

### PUT /api/v1/facturacion-clientes/{id}
- Actualiza un registro de facturación.
- Body JSON similar al de creación.
- Respuesta: `200 OK` o `404 Not Found`.

### DELETE /api/v1/facturacion-clientes/{id}
- Elimina un registro de facturación.
- Respuesta: `204 No Content`.
