# API: Proveedores

Base path: `/api/v1/proveedores`

## Endpoints

### GET /api/v1/proveedores
- Devuelve todos los proveedores.
- Respuesta: `200 OK` con lista JSON.

### GET /api/v1/proveedores/{id}
- Devuelve el proveedor con el ID especificado.
- Respuesta: `200 OK` o `404 Not Found`.

### POST /api/v1/proveedores
- Crea un proveedor.
- Body JSON requerido:
```json
{
  "nombreProveedor": "Proveedor XYZ",
  "datosContacto": "atencion@xyz.cl",
  "email": "atencion@xyz.cl",
  "telefono": "912345678",
  "direccion": "Calle 123"
}
```
- Respuesta: `201 Created` con el proveedor creado.

### PUT /api/v1/proveedores/{id}
- Actualiza un proveedor existente.
- Body JSON opcional:
```json
{
  "nombreProveedor": "Proveedor Actualizado",
  "datosContacto": "ventas@xyz.cl",
  "email": "ventas@xyz.cl",
  "telefono": "998877665",
  "direccion": "Avenida 456"
}
```
- Respuesta: `200 OK` con el proveedor actualizado o `404 Not Found`.

### DELETE /api/v1/proveedores/{id}
- Elimina un proveedor.
- Respuesta: `200 OK` con mensaje de éxito o `404 Not Found`.
