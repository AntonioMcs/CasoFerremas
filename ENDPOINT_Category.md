# Documentación de API REST de Categorías

## Descripción general

Esta documentación describe los endpoints disponibles en la API REST de categorías desarrollada con Spring Boot. La API permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre categorías almacenadas en una base de datos.
Hecho por Gustavo Santana
## Base URL

Todos los endpoints están precedidos por la siguiente URL base:

```
http://localhost:8080/api/v1
```

## Modelo de datos

El modelo de datos de una categoría contiene los siguientes campos:

| Campo | Tipo    | Descripción                                   | Requerido         |
|-------|---------|-----------------------------------------------|-------------------|
| id    | Integer | Identificador único generado automáticamente  | No (generado)     |
| nombre| String  | Nombre de la categoría                        | Sí                |

## Endpoints disponibles

### 1. Crear una nueva categoría

Permite registrar una nueva categoría en el sistema.

- **URL**: `/category/post`
- **Método**: `POST`
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre": "Nombre de la categoría"
}
```

- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Nombre de la categoría"
}
```

### 2. Obtener todas las categorías

Recupera la lista completa de categorías almacenadas en el sistema, ordenadas por id ascendente.

- **URL**: `/categories`
- **Método**: `GET`
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
    {
        "id": 1,
        "nombre": "Categoría A"
    },
    {
        "id": 2,
        "nombre": "Categoría B"
    }
]
```

### 3. Obtener una categoría por ID

Recupera la información de una categoría específica según su ID.

- **URL**: `/category/{id}`
- **Método**: `GET`
- **Parámetros de ruta**:
  - `id`: Identificador único de la categoría
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Categoría A"
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

### 4. Actualizar una categoría existente

Permite modificar los datos de una categoría existente identificada por su ID.

- **URL**: `/updCategory/{id}`
- **Método**: `PUT`
- **Parámetros de ruta**:
  - `id`: Identificador único de la categoría a actualizar
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre": "Nombre actualizado"
}
```

- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "Nombre actualizado"
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

### 5. Eliminar una categoría

Permite eliminar una categoría existente del sistema.

- **URL**: `/DelCategory/{id}`
- **Método**: `DELETE`
- **Parámetros de ruta**:
  - `id`: Identificador único de la categoría a eliminar
- **Respuesta exitosa**:
  - **Código**: 204 (No Content)
  - **Contenido**: Sin contenido
- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

## Guía para pruebas con Thunder Client

Para probar los endpoints de esta API utilizando Thunder Client, siga los siguientes pasos:

### Configuración inicial

1. Instale la extensión Thunder Client desde el marketplace de Visual Studio Code.
2. Asegúrese de que su aplicación Spring Boot esté en ejecución localmente.

### Probar endpoint de creación (POST)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `POST`.
3. Ingrese la URL: `http://localhost:8080/api/v1/category/post`.
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto de categoría válido.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 200 y los datos de la categoría creada.

### Probar endpoints de consulta (GET)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `GET`.
3. Ingrese la URL para el endpoint deseado:
   - Para todas las categorías: `http://localhost:8080/api/v1/categories`
   - Para una categoría específica: `http://localhost:8080/api/v1/category/1` (donde 1 es el ID)
4. Ejecute la solicitud y verifique la respuesta recibida.

### Probar endpoint de actualización (PUT)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `PUT`.
3. Ingrese la URL: `http://localhost:8080/api/v1/updCategory/1` (donde 1 es el ID de la categoría a actualizar).
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto con los datos actualizados.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 200 y los datos actualizados.

### Probar endpoint de eliminación (DELETE)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `DELETE`.
3. Ingrese la URL: `http://localhost:8080/api/v1/DelCategory/1` (donde 1 es el ID de la categoría a eliminar).
4. Ejecute la solicitud y verifique que reciba un código de estado 204.
5. Para confirmar la eliminación, realice una solicitud GET al mismo recurso y verifique que reciba un código 404.