# API: Productos

Base path: `/api/v1/productos`

## Endpoints

### GET /api/v1/productos
- Devuelve todos los productos.
- Respuesta: `200 OK` con lista JSON.

### GET /api/v1/productos/{id}
- Devuelve un producto por ID.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/productos
- Crea un nuevo producto.
- Body JSON requerido:
```json
{
  "nombreProducto": "Taladro eléctrico",
  "marca": "MarcaX",
  "descripcion": "Taladro 500W",
  "precio": 129990,
  "stock": 10,
  "unidadMedida": "unidad",
  "codigoSku": "TAL-500",
  "categoriaId": 1,
  "proveedorId": 2
}
```
- `categoriaId` y `proveedorId` son opcionales.
- Respuesta: `201 Created` con mensaje de éxito.

### PUT /api/v1/productos/{id}
- Actualiza un producto.
- Body JSON opcional:
```json
{
  "precio": 119990,
  "stock": 15,
  "proveedorId": 3
}
```
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.

### DELETE /api/v1/productos/{id}
- Elimina un producto.
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.
