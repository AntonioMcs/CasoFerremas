# API: Usuarios

Base path: `/api/v1/usuarios`

## Endpoints

### GET /api/v1/usuarios
- Devuelve todos los usuarios registrados.
- Respuesta: `200 OK` con lista JSON de usuarios.

### GET /api/v1/usuarios/{id}
- Devuelve el usuario con el ID especificado.
- Respuesta: `200 OK` con objeto JSON o `404 Not Found` si no existe.

### GET /api/v1/usuarios/tipo/{tipoUsuario}
- Devuelve usuarios filtrados por tipo de usuario.
- Respuesta: `200 OK` con lista JSON.

### POST /api/v1/usuarios
- Crea un nuevo usuario.
- Body JSON requerido:
```json
{
  "nombre": "Juan Perez",
  "email": "juan@correo.com",
  "contrasena": "secreto123",
  "tipoUsuario": "cliente"
}
```
- Respuesta: `201 Created` con el usuario creado.

### PUT /api/v1/usuarios/{id}
- Actualiza un usuario existente.
- Body JSON requerido:
```json
{
  "nombre": "Juan Perez",
  "email": "juan@correo.com",
  "contrasena": "nuevo123",
  "tipoUsuario": "cliente"
}
```
- Respuesta: `200 OK` con el usuario actualizado o `404 Not Found`.

### DELETE /api/v1/usuarios/{id}
- Elimina un usuario.
- Respuesta: `204 No Content` si se elimina.
