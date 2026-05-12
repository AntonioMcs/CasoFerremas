# FERREMAS - Proyecto de Microservicios con Spring Boot

## 1. Descripcion del caso

FERREMAS es un sistema de gestion comercial para una ferreteria, orientado a:

- Gestion de usuarios
- Gestion de productos y categorias
- Gestion de pedidos
- Gestion de pagos y despachos
- Soporte de carrito de compras

Este repositorio contiene la linea base backend construida con Spring Boot, JPA/Hibernate y PostgreSQL (Supabase), con operaciones CRUD para las entidades principales del caso.

## 2. Objetivo de esta entrega

Desarrollar una base funcional del caso FERREMAS donde cada integrante implemente un microservicio distinto, con:

- Conexion activa a Supabase PostgreSQL
- Entidad y modelo de datos definidos
- CRUD completo operativo
- Pruebas con ThunderClient
- Evidencia de trabajo colaborativo con Git

## 3. Tecnologias usadas

- Java 21
- Spring Boot 3.4.x
- Spring Web
- Spring Data JPA
- Hibernate ORM
- PostgreSQL Driver
- Spring Boot Actuator
- Swagger/OpenAPI (springdoc)
- Maven Wrapper

## 4. Estructura general del backend

Paquetes principales:

- model: entidades JPA
- repository: acceso a datos
- controller: endpoints REST
- dto: objetos de transferencia de datos
- config: carga/configuracion inicial

Entidades principales del caso FERREMAS:

- Usuario
- Product
- Category
- Pedido
- EstadoPedido
- DetallePedido
- CarritoCompra
- Pago
- Despacho

## 5. Modelo de datos del caso FERREMAS

Tablas principales consideradas en el proyecto:

- usuarios
- productos
- category
- pedidos
- estados_pedido
- detalle_pedido
- carrito_compras
- pagos
- despachos

Relaciones relevantes:

- Pedido -> Usuario
- Pedido -> EstadoPedido
- DetallePedido -> Pedido
- DetallePedido -> Product
- CarritoCompra -> Usuario
- CarritoCompra -> Product
- Pago -> Pedido (1 a 1)
- Despacho -> Pedido (1 a 1)

## 6. Endpoints principales

Base URL local:

- http://localhost:8080

Rutas CRUD implementadas (resumen):

- /api/v1/usuarios
- /api/v1/products
- /api/v1/categories
- /api/v1/pedidos
- /api/v1/estados-pedido
- /api/v1/detalle-pedidos
- /api/v1/carrito-compras
- /api/v1/pagos
- /api/v1/despachos

Cada recurso debe exponer:

- POST crear
- GET listar todos
- GET buscar por id
- PUT actualizar por id
- DELETE eliminar por id

## 7. Ejecucion local

### 7.1 Requisitos

- Java 21 instalado
- Acceso a internet (descarga de dependencias Maven)
- Credenciales validas de Supabase PostgreSQL

### 7.2 Configuracion

Editar application.properties con:

- spring.datasource.url
- spring.datasource.username
- spring.datasource.password

### 7.3 Comandos

Compilar:

```bash
./mvnw.cmd -q -DskipTests compile
```

Ejecutar:

```bash
./mvnw.cmd spring-boot:run
```

Validar arranque:

- Tomcat iniciado en puerto 8080
- Conexion a PostgreSQL establecida
- Entidades sincronizadas por Hibernate (segun configuracion)

## 8. Pruebas con ThunderClient

Checklist minimo por microservicio:

1. POST crea un registro valido
2. GET lista todos los registros
3. GET por id devuelve el registro correcto
4. PUT actualiza datos del registro
5. DELETE elimina el registro esperado

Evidencia recomendada:

- Capturas de request/response
- Export de coleccion ThunderClient
- Ejemplos de payload JSON por recurso

## 9. Distribucion de microservicios por integrante

Cada integrante debe tomar un dominio distinto de FERREMAS. Ejemplo de distribucion para 3 personas:

1. Microservicio de catalogo
   - productos
   - categorias
2. Microservicio de usuarios
   - usuarios
   - validaciones de estado activo
3. Microservicio de pedidos
   - pedidos
   - detalle_pedido
   - pagos
   - despachos
   - carrito_compras

Regla clave:

- No repetir el mismo tipo de microservicio entre integrantes.

## 10. Buenas practicas evaluadas

- Separacion de responsabilidades (Controller, Service, Repository)
- DTOs para entrada y salida cuando corresponda
- Validaciones de negocio y de datos
- Manejo correcto de codigos HTTP
- Codigo limpio y mantenible
- Commits frecuentes y claros en Git
- Documentacion de rutas y pruebas

## 11. Entregables del caso FERREMAS

1. Proyecto funcional por integrante (o carpeta separada por microservicio)
2. Conexion operativa a Supabase
3. CRUD completo probado con ThunderClient
4. Evidencia de Git (historial de commits)
5. Documento grupal con:
   - que hizo cada integrante
   - que microservicio implemento
   - como se integra con FERREMAS

## 12. Criterios de evaluacion (resumen)

La evaluacion considera:

- Diseno tecnico del microservicio
- Configuracion de Spring + Maven + Git
- Desarrollo funcional CRUD con JPA/ORM
- Integracion RESTful y validacion en ThunderClient
- Defensa individual en presentacion oral

## 13. API docs

Si Swagger esta habilitado, revisar:

- http://localhost:8080/swagger-ui/index.html

## 14. Estado actual del repositorio

En esta version del caso FERREMAS ya se encuentra:

- Conexion a Supabase configurada
- Entidades principales de negocio implementadas
- Repositories JPA creados
- Controladores REST para operaciones CRUD base

Pendientes recomendados para una siguiente iteracion:

- Capa Service en todos los dominios
- Manejo de errores global con @ControllerAdvice
- Pruebas unitarias e integracion por endpoint
- Documentacion de payloads por recurso

## 15. Integrantes

Completar con datos reales del equipo:

- Integrante 1: Nombre - Microservicio asignado
- Integrante 2: Nombre - Microservicio asignado
- Integrante 3: Nombre - Microservicio asignado
