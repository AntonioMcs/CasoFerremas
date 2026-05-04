# Documentación de API REST de Reseñas

## Descripción general

Esta documentación describe los endpoints disponibles en la API REST de reseñas desarrollada con Spring Boot. La API permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre reseñas almacenadas en una base de datos.

## Base URL

Todos los endpoints están precedidos por la siguiente URL base:

```
http://localhost:8080/api/v1
```

## Modelo de datos

El modelo de datos de una reseña contiene los siguientes campos:

| Campo       | Tipo    | Descripción                                      | Requerido |
|-------------|---------|--------------------------------------------------|-----------|
| id          | Integer | Identificador único de la reseña (autogenerado)  | No        |
| nombre      | String  | Nombre de la reseña                              | Sí        |
| comentario  | String  | Comentario de la reseña                          | Sí        |
| calificacion| int     | Calificación del 1 al 5                          | Sí        |
| usuarioId   | int     | ID del usuario que publicó la reseña              | Sí        |

## Endpoints disponibles

### 1. Crear una reseña

Permite registrar una reseña nueva en el sistema.

- **URL**: `/review/post`
- **Método**: `POST`
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
  "nombre": "Reseña ejemplo",
  "comentario": "Muy buen producto",
  "calificacion": 5,
  "usuarioId": 1
}
```

- **Respuesta exitosa**:
  - **Código**: 201 (Created)
  - **Contenido**:

```json
{
  "id": 2,
  "nombre": "Reseña ejemplo",
  "comentario": "Muy buen producto",
  "calificacion": 5,
  "usuario": {
    "id": 1,
    "nombre": "usuario1",
    ...
  }
}
```

### 2. Obtener todas las reseñas

Recupera la lista completa de reseñas almacenadas en el sistema.

- **URL**: `/reviews`
- **Método**: `GET`
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
  {
    "id": 1,
    "nombre": "Reseña 1",
    "comentario": "comentario",
    "calificacion": 4,
    "usuario": { "id": 1, "nombre": "usuario1" }
  },
  {
    "id": 2,
    "nombre": "Reseña 2",
    "comentario": "comentario2",
    "calificacion": 5,
    "usuario": { "id": 2, "nombre": "usuario2" }
  }
]
```

### 3. Obtener una reseña por ID

Recupera la información de una reseña específica según su ID.

- **URL**: `/review/{id}`
- **Método**: `GET`
- **Parámetros de ruta**:
  - `id`: Identificador único de la reseña
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
  "id": 1,
  "nombre": "Reseña 1",
  "comentario": "comentario",
  "calificacion": 4,
  "usuario": { "id": 1, "nombre": "usuario1" }
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

### 4. Actualizar una reseña existente

Permite modificar los datos de una reseña existente identificada por su ID.

- **URL**: `/review/{id}`
- **Método**: `PUT`
- **Parámetros de ruta**:
  - `id`: Identificador único de la reseña a actualizar.
- **Cuerpo de la solicitud**:

```json
{
  "nombre": "Reseña actualizada",
  "comentario": "comentario actualizado",
  "calificacion": 3,
  "usuarioId": 1
}
```
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:
```json
{
  "id": 1,
  "nombre": "Reseña actualizada",
  "comentario": "comentario actualizado",
  "calificacion": 3,
  "usuario": { "id": 1, "nombre": "usuario1" }
}
```

### 5. Eliminar una reseña

Permite eliminar una reseña existente identificada por su ID.

- **URL**: `/delReview/{id}`
- **Método**: `DELETE`
- **Parámetros de ruta**:
  - `id`: Identificador único de la reseña a eliminar
- **Respuesta exitosa**:
  - **Código**: 204 (No Content)
  - **Contenido**: Sin contenido
- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

## Notas
- El campo usuarioId debe corresponder a un usuario existente en la base de datos.
- El campo calificacion debe estar entre 1 y 5.
- El campo usuario en la respuesta es un objeto con los datos del usuario asociado.