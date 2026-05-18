# API: Inventarios

Base path: `/api/v1/inventarios`

## Endpoints

### GET /api/v1/inventarios
- Devuelve todos los inventarios.
- Respuesta: `200 OK` con lista JSON.

### GET /api/v1/inventarios/{id}
- Devuelve un inventario por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/inventarios
- Crea un inventario.
- Body JSON requerido:
```json
{
  "productoId": 1,
  "stockActual": 50,
  "stockMinimo": 10,
  "ubicacionBodega": "Bodega A",
  "proveedorId": 2
}
```
- `proveedorId` es opcional.
- Respuesta: `201 Created` con mensaje de éxito.

### PUT /api/v1/inventarios/{id}
- Actualiza un inventario.
- Body JSON opcional:
```json
{
  "stockActual": 45,
  "stockMinimo": 5,
  "proveedorId": 3
}
```
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.

### DELETE /api/v1/inventarios/{id}
- Elimina un inventario.
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.
