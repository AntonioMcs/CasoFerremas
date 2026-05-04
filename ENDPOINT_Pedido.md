# Documentación de API REST de Pedidos

## Descripción general

Esta documentación describe los endpoints disponibles en la API REST de Pedidos desarrollada con Spring Boot. La API permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre pedidos almacenados en una base de datos.

## Base URL

Todos los endpoints están precedidos por la siguiente URL base:

```
http://localhost:8080/api/v1/pedido
```

## Modelo de datos

El modelo de datos de un usuario contiene los siguientes campos:

| Campo     | Tipo    | Descripción                                              | Requerido |
|-----------|---------|----------------------------------------------------------|-----------|
| id        | Integer | Identificador único generado automáticamente             | No        |
| nombre    | String  | Nombre del pedido                                        | Sí        |
| precio    | double  | el precio que tendra el pedido                           | Sí        |
| categoria | Category| la categoria del pedido se ve por su id                  | Sí        |
|descripcion| String  | descripcion del pedido (que quiere el cliente que lleve) | Sí        |
|usuario    | usuario | es el id del usuario que hiso el pedido                  | Sí        |

## Endpoints disponibles

### 1. Crear un nuevo pedido

Permite registrar un nuevo pedido en el sistema.

- **URL**: `/pedido`
- **Método**: `POST`
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre": "poleron",
    "categoria": 2,
    "precio": 11000.00,
    "descripcion": "poleron tipo pokemon",
    "usuarioId": 2
}
```

- **Respuesta exitosa**:
  - **Código**: 201 (Created)
  - **Contenido**:

```json
{
    "id": 1,
    "nombre": "poleron",
    "categoria": 2,
    "precio": 11000.00,
    "descripcion": "poleron tipo pokemon",
    "usuarioId": 2
}
```

---

### 2. Obtener todos los Pedidos

Recupera la lista completa de los pedidos almacenados en el sistema.

- **URL**: `/pedidos`
- **Método**: `GET`
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
    {
       "id": 1,
        "nombre": "poleron",
        "categoria": 2,
        "precio": 11000.00,
        "descripcion": "poleron tipo pokemon",
        "usuarioId": 2
    },
    {
        "id": 2,
        "nombre": "polera",
        "categoria": 1,
        "precio": 10000.00,
        "descripcion": "poleron logo doraemon",
        "usuarioId": 2
    }
]
```

---

### 3. Obtener un pedido por ID

Recupera la información de un pedido específico según su ID.

- **URL**: `/pedido/{id}`
- **Método**: `GET`
- **Parámetros de ruta**:
  - `id`: Identificador único del pedido
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "id": 2,
    "nombre": "polera",
    "categoria": 1,
    "precio": 10000.00,
    "descripcion": "poleron logo doraemon",
    "usuarioId": 2
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

---

### 4. Filtrar pedido por id de categoria

muestra la lista de peidos buscado por el id de categoria.

- **URL**: `/pedidos/categoria/{categoria}`
- **Método**: `GET`
- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
[
    {
    "nombre" : "poleron",
    "categoria" : 2,
    "precio" : 11000.00,
    "descripcion" : "poleron con logo de pokemon",
    "usuarioId" : 2
    }
]
```

---

### 5. Actualizar un pedido existente

Permite modificar los datos de un pedido existente identificado por su ID.

- **URL**: `/pedido/{id}`
- **Método**: `PUT`
- **Parámetros de ruta**:
  - `id`: Identificador único del pedido a actualizar
- **Encabezados**:
  - Content-Type: application/json
- **Cuerpo de la solicitud**:

```json
{
    "nombre" : "poleron",
    "categoria" : 2,
    "precio" : 11000.00,
    "descripcion" : "poleron con logo de pokemon",
    "usuarioId" : 2
}
```

- **Respuesta exitosa**:
  - **Código**: 200 (OK)
  - **Contenido**:

```json
{
    "nombre" : "poleron modificado",
    "categoria" : 2,
    "precio" : 11000.00,
    "descripcion" : "poleron con logo de pokemon modificado",
    "usuarioId" : 2
}
```

- **Respuesta de error**:
  - **Código**: 404 (Not Found)
  - **Contenido**: Sin contenido

---

### 6. Eliminar un pedido

Permite eliminar un pedido existente del sistema.

- **URL**: `/pedido/{id}`
- **Método**: `DELETE`
- **Parámetros de ruta**:
  - `id`: Identificador único del pedido a eliminar
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
3. Ingrese la URL: `http://localhost:8080/api/v1/pedido/post`.
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto de usuario válido.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 201 y los datos del usuario creado.

### Probar endpoints de consulta (GET)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `GET`.
3. Ingrese la URL para el endpoint deseado:
   - Para todos los pedido: `http://localhost:8080/api/v1/pedido/get`
   - Para un usuario específico: `http://localhost:8080/api/v1/pedido/get/1` (donde 1 es el ID)
   - Para pedido por rol: `http://localhost:8080/api/v1/pedido/rol/admin`
4. Ejecute la solicitud y verifique la respuesta recibida.

### Probar endpoint de actualización (PUT)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `PUT`.
3. Ingrese la URL: `http://localhost:8080/api/v1/pedido/put/1` (donde 1 es el ID del usuario a actualizar).
4. En la pestaña "Body", seleccione formato "JSON" e ingrese un objeto con los datos actualizados.
5. En la pestaña "Headers", asegúrese de incluir `Content-Type: application/json`.
6. Ejecute la solicitud y verifique que reciba un código de estado 200 y los datos actualizados.

### Probar endpoint de eliminación (DELETE)

1. Cree una nueva solicitud en Thunder Client.
2. Configure el método como `DELETE`.
3. Ingrese la URL: `http://localhost:8080/api/v1/pedido/delete/1` (donde 1 es el ID del usuario a eliminar).
4. Ejecute la solicitud y verifique que reciba un código de estado 204.
5. Para confirmar la eliminación, realice una solicitud GET al mismo recurso y verifique que reciba un código 404.

---