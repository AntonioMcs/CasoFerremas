# API: Categorías

Base path: `/api/v1/categorias`

## Endpoints

### GET /api/v1/categorias
- Devuelve todas las categorías ordenadas por ID.
- Respuesta: `200 OK` con lista JSON.

### GET /api/v1/categorias/{id}
- Devuelve la categoría con el ID especificado.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/categorias
- Crea una nueva categoría.
- Body JSON requerido:
```json
{
  "nombreCategoria": "Herramientas"
}
```
- Respuesta: `201 Created` con la categoría creada.

### PUT /api/v1/categorias/{id}
- Actualiza el nombre de una categoría.
- Body JSON requerido:
```json
{
  "nombreCategoria": "Herramientas eléctricas"
}
```
- Respuesta: `200 OK` con la categoría actualizada o `404 Not Found`.

### DELETE /api/v1/categorias/{id}
- Elimina una categoría.
- Respuesta: `204 No Content` si se elimina.
