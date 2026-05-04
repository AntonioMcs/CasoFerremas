# Documentación de API REST de Usuarios

## Descripción general

Esta documentación describe los endpoints disponibles en la API REST de usuarios desarrollada con Spring Boot. La API permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre usuarios almacenados en una base de datos.

## Base URL

Todos los endpoints están precedidos por la siguiente URL base:

```
http://localhost:8080/api/v1/usuarios
```

## Modelo de datos

El modelo de datos de un usuario contiene los siguientes campos:

| Campo    | Tipo    | Descripción                                   | Requerido |
|----------|---------|-----------------------------------------------|-----------|
| id       | Integer | Identificador único generado automáticamente  | No        |
| nombre   | String  | Nombre del usuario                            | Sí        |
| email    | String  | Correo electrónico único y válido             | Sí        |
| password | String  | Contraseña del usuario                        | Sí        |
| rol      | String  | Rol del usuario (ej: admin, user, etc.)       | Sí        |

## Endpoints disponibles

### 1. Crear un nuevo usuario

Permite registrar un nuevo usuario en el sistema.

- **URL**: `/post`
- **Método**: `POST`
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre": "Pedro",
    "email": "pedropedropedrope@gmail.com",
    "password": "balatro",
    "rol": "admin"
}
```

- **Respuesta exitosa**:
  - **Código**: 201 (Created)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Pedro",
    "email": "pedropedropedrope@gmail.com",
    "rol": "admin"
}
```
> **Nota:** El campo `password` no se muestra en la respuesta por seguridad.

---

### 2. Obtener todos los usuarios

Recupera la lista completa de usuarios almacenados en el sistema.

- **URL**: `/get`
- **Método**: `GET`
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
    {
        "id": 1,
        "nombre": "Pedro",
        "email": "pedro@email.com",
        "rol": "admin"
    },
    {
        "id": 2,
        "nombre": "Ana",
        "email": "ana@email.com",
        "rol": "user"
    }
]
```

---

### 3. Obtener un usuario por ID

Recupera la información de un usuario específico según su ID.

- **URL**: `/get/{id}`
- **Método**: `GET`
- **Parámetros de ruta**:
  - `id`: Identificador único del usuario
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Pedro",
    "email": "pedro@email.com",
    "rol": "admin"
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

---

### 4. Filtrar usuarios por rol

Recupera una lista de usuarios que tienen un rol específico.

- **URL**: `/rol/{rol}`
- **Método**: `GET`
- **Parámetros de ruta**:
  - `rol`: Rol por el que filtrar (ej: admin, user)
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
    {
        "id": 1,
        "nombre": "Pedro",
        "email": "pedro@email.com",
        "rol": "admin"
    }
]
```

---

### 5. Actualizar un usuario existente

Permite modificar los datos de un usuario existente identificado por su ID.

- **URL**: `/put/{id}`
- **Método**: `PUT`
- **Parámetros de ruta**:
  - `id`: Identificador único del usuario a actualizar
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre": "Pedro Actualizado",
    "email": "pedro@email.com",
    "password": "nuevaClave",
    "rol": "user"
}
```

- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Pedro Actualizado",
    "email": "pedro@email.com",
    "rol": "user"
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

---

### 6. Eliminar un usuario

Permite eliminar un usuario existente del sistema.

- **URL**: `/delete/{id}`
- **Método**: `DELETE`
- **Parámetros de ruta**:
  - `id`: Identificador único del usuario a eliminar
- **Respuesta exitosa**:
  - **Código**: 204 (No Content)
  - **Contenido**: Sin contenido
- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

---

## Guía para pruebas con Thunder Client

Para probar los endpoints de esta API utilizando Thunder Client, siga los siguientes pasos:

### Configuración inicial

1. Instale la extensión Thunder Client desde el marketplace de Visual Studio Code.
2. Asegúrese de que su aplicación Spring Boot esté en ejecución localmente.

### Probar endpoint de creación (POST)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `POST`.
3. Ingrese la URL: `http://localhost:8080/api/v1/usuarios/post`.
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto de usuario válido.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 201 y los datos del usuario creado.

### Probar endpoints de consulta (GET)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `GET`.
3. Ingrese la URL para el endpoint deseado:
   - Para todos los usuarios: `http://localhost:8080/api/v1/usuarios/get`
   - Para un usuario específico: `http://localhost:8080/api/v1/usuarios/get/1` (donde 1 es el ID)
   - Para usuarios por rol: `http://localhost:8080/api/v1/usuarios/rol/admin`
4. Ejecute la solicitud y verifique la respuesta recibida.

### Probar endpoint de actualización (PUT)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `PUT`.
3. Ingrese la URL: `http://localhost:8080/api/v1/usuarios/put/1` (donde 1 es el ID del usuario a actualizar).
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto con los datos actualizados.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 200 y los datos actualizados.

### Probar endpoint de eliminación (DELETE)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `DELETE`.
3. Ingrese la URL: `http://localhost:8080/api/v1/usuarios/delete/1` (donde 1 es el ID del usuario a eliminar).
4. Ejecute la solicitud y verifique que reciba un código de estado 204.
5. Para confirmar la eliminación, realice una solicitud GET al mismo recurso y verifique que reciba un código 404.

---