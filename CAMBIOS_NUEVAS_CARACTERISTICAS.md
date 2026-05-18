# 📝 ACTUALIZACIÓN DE NUEVAS CARACTERÍSTICAS - CasoFerremas

## ✅ CAMBIOS REALIZADOS

### 1. 🏭 TABLA PROVEEDOR (Nueva)

Se ha creado una nueva tabla `Proveedor` que está conectada a `Producto` e `Inventario`.

#### Estructura Proveedor:
- **id_proveedor**: Identificador único (auto-generado)
- **nombre_proveedor**: Nombre del proveedor (requerido)
- **datos_contacto**: Información de contacto (requerido)
- **email**: Email del proveedor (opcional)
- **telefono**: Teléfono de contacto (opcional)
- **direccion**: Dirección del proveedor (opcional)

#### Relaciones:
- **1:N con Producto** → Un proveedor puede tener múltiples productos
- **1:N con Inventario** → Un proveedor puede estar asociado a múltiples registros de inventario

---

### 2. 🛒 ENDPOINTS PROVEEDOR

#### Listar todos los proveedores
```
GET /api/v1/proveedores
Respuesta: [Proveedor]
Advertencia: ⚠️ Si no hay proveedores registrados
```

#### Obtener proveedor por ID
```
GET /api/v1/proveedores/{id}
Parámetro: id (Integer, requerido)
Respuesta: Proveedor
Advertencia: ⚠️ Si el proveedor no existe
```

#### Crear nuevo proveedor
```
POST /api/v1/proveedores
Body JSON:
{
  "nombreProveedor": "Distribuidora XYZ",
  "datosContacto": "Contacto principal",
  "email": "contacto@distribuidor.com",
  "telefono": "+56912345678",
  "direccion": "Calle 123, Santiago"
}
Respuesta: Proveedor creado
Advertencia: ⚠️ Si el nombre ya existe
```

#### Actualizar proveedor
```
PUT /api/v1/proveedores/{id}
Parámetro: id (Integer, requerido)
Body JSON: (Actualizar los campos necesarios)
Respuesta: Proveedor actualizado
Advertencia: ⚠️ Si el proveedor no existe
```

#### Eliminar proveedor
```
DELETE /api/v1/proveedores/{id}
Parámetro: id (Integer, requerido)
Respuesta: Mensaje de éxito
Advertencia: ⚠️ Si el proveedor no existe
```

---

### 3. 💳 INTEGRACIÓN TRANSBANK

Se ha integrado **Transbank WebPay Plus** al módulo de pagos.

#### Endpoints de Transbank:

#### Procesar pago con Transbank
```
POST /api/v1/pagos/{pagoId}/transbank
Parámetros:
  - pagoId (Integer, PATH) - ID del pago a procesar
  - retorno (String, QUERY) - URL de retorno después del pago
  
Respuesta:
{
  "message": "✓ Transacción iniciada",
  "token": "TOKEN_xxxxx",
  "orden": "ORD_123"
}

Advertencias:
  ⚠️ Si el pagoId es inválido
  ⚠️ Si el pago ya fue procesado
  ⚠️ Si la URL de retorno no es válida
```

#### Verificar estado de transacción Transbank
```
GET /api/v1/pagos/{pagoId}/transbank/estado/{token}
Parámetros:
  - pagoId (Integer, PATH) - ID del pago
  - token (String, PATH) - Token de transacción
  
Respuesta:
{
  "message": "✓ Pago autorizado",
  "status": "AUTHORIZED"
}

Advertencias:
  ⚠️ Si el pagoId es inválido
  ⚠️ Si la transacción fue rechazada
  ⚠️ Si hay error de conexión con Transbank
```

---

### 4. ⚠️ ADVERTENCIAS EN TODOS LOS ENDPOINTS

Se han agregado advertencias (warnings) en todos los endpoints existentes. Ahora cada operación devuelve:

#### En caso de éxito (✓):
```json
{
  "message": "✓ Operación completada exitosamente | ID: 123"
}
```

#### En caso de advertencia (⚠️):
```json
{
  "error": "⚠️ Error de validación",
  "detalles": {
    "campo": "⚠️ Mensaje de error específico"
  }
}
```

#### Tipos de advertencias implementadas:

| Endpoint | Advertencias |
|----------|--------------|
| **GET** | ⚠️ No hay registros / ⚠️ Recurso no encontrado |
| **POST** | ⚠️ Campos requeridos vacíos / ⚠️ Datos inválidos |
| **PUT** | ⚠️ Recurso no encontrado / ⚠️ No se puede modificar |
| **DELETE** | ⚠️ Recurso no encontrado / ⚠️ No se puede eliminar |
| **Errores** | ⚠️ Error del servidor / ⚠️ Error de conexión |

---

### 5. 🌍 MANEJADOR GLOBAL DE EXCEPCIONES

Se ha creado `GlobalExceptionHandler` que captura todas las excepciones y devuelve respuestas consistentes.

#### Excepciones manejadas:
- ✓ Validación de datos (MethodArgumentNotValidException)
- ✓ Resource No Encontrado (NoHandlerFoundException)
- ✓ Argumentos inválidos (IllegalArgumentException)
- ✓ Excepciones genéricas (Exception)
- ✓ Excepciones de runtime (RuntimeException)

#### Respuesta de error estándar:
```json
{
  "timestamp": "2026-05-17T10:30:45.123",
  "status": 400,
  "error": "⚠️ Error de validación",
  "detalles": {
    "nombreProducto": "⚠️ El nombre no puede estar vacío"
  },
  "path": "/api/v1/productos"
}
```

---

### 6. 📦 CAMBIOS EN MODELOS

#### Product (Actualizado)
Se agregó relación ManyToOne con Proveedor:
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_proveedor")
private Proveedor proveedor;
```

#### Inventario (Actualizado)
Se agregó relación ManyToOne con Proveedor:
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_proveedor")
private Proveedor proveedor;
```

---

### 7. 👁️ CAMBIOS EN DTOs

#### ProductDTO
Se agregó field:
```java
private Integer proveedorId;
```

#### InventarioDTO
Se agregó field:
```java
private Integer proveedorId;
```

---

### 8. 📚 DEPENDENCIAS AGREGADAS

Se agregó al `pom.xml`:
```xml
<!-- Transbank SDK -->
<dependency>
    <groupId>com.transbank</groupId>
    <artifactId>transbank-sdk</artifactId>
    <version>1.12.0</version>
</dependency>
```

---

## 🚀 CÓMO USAR LAS NUEVAS CARACTERÍSTICAS

### Ejemplo 1: Crear un Proveedor
```bash
curl -X POST http://localhost:8080/api/v1/proveedores \
  -H "Content-Type: application/json" \
  -d '{
    "nombreProveedor": "Proveedora del Sur",
    "datosContacto": "Carlos López",
    "email": "carlos@suministros.com",
    "telefono": "+56987654321"
  }'
```

### Ejemplo 2: Crear Producto con Proveedor
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombreProducto": "Tornillo 5mm",
    "marca": "Ferretería Plus",
    "precio": 250.00,
    "stock": 100,
    "proveedorId": 1,
    "categoriaId": 5
  }'
```

### Ejemplo 3: Procesar Pago con Transbank
```bash
# 1. Crear un pago primero (POST /api/v1/pagos)
# 2. Luego procesar con Transbank
curl -X POST "http://localhost:8080/api/v1/pagos/5/transbank?retorno=http://localhost:3000/pago-completado" \
  -H "Content-Type: application/json"

# 3. Verificar estado
curl -X GET "http://localhost:8080/api/v1/pagos/5/transbank/estado/TOKEN_xxxxx"
```

---

## ⚙️ CONFIGURACIÓN TRANSBANK (Importante)

Para usar Transbank en producción, debes:

1. Registrarte en https://www.transbank.cl
2. Obtener tu código de comercio y API key
3. Actualizar la clase `TransbankService.java`:

```java
private static final String COMMERCE_CODE = "TU_CODIGO";
private static final String API_KEY = "TU_API_KEY";

public TransbankService() {
    // Descomenta estas líneas en producción
    // Transbank.setCommerceCode(COMMERCE_CODE);
    // Transbank.setApiKey(API_KEY);
}
```

---

## 📝 NOTAS IMPORTANTES

1. ✓ Todos los endpoints devuelven mensajes de advertencia en caso de error
2. ✓ Se validam todos los datos entrada
3. ✓ Se manejan todas las excepciones globalmente
4. ✓ Las transacciones Transbank actualmente están en modo simulación
5. ✓ Las relaciones de Proveedor con Producto e Inventario están configuradas correctamente

---

## 🔍 PRÓXIMOS PASOS

Para completar la integración de Transbank:
1. Crear credenciales en Transbank
2. Implementar URL de retorno en el frontend
3. Procesar autenticación 3D Secure
4. Agregar logs de todas las transacciones
5. Crear reportes de pagos

---

**Última actualización:** 17 de Mayo, 2026
**Versión:** 1.1.0
