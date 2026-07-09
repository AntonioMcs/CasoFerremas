# ✅ GUÍA RÁPIDA DE PETICIONES - Ferrería API

## 🔴 IMPORTANTE - ESTRUCTURA DEL JSON

### ❌ INCORRECTO (Lo que está generando el error):
```json
{
  "id": 1,
  "nombreCategoria": "Herrajes"
}
```

### ✅ CORRECTO (Lo que debes enviar):
```json
{
  "nombreCategoria": "Herrajes"
}
```

**⚠️ NO incluyas el campo `id` en los POSTs. La BD lo genera automáticamente.**

---

## 📋 PETICIONES CORRECTAS

### 1️⃣ CREAR CATEGORÍAS

**Método:** POST  
**URL:** `http://localhost:8080/api/v1/categorias`  
**Headers:** `Content-Type: application/json`

#### Herrajes
```json
{
  "nombreCategoria": "Herrajes"
}
```

#### Tuberías
```json
{
  "nombreCategoria": "Tuberías"
}
```

#### Pinturas
```json
{
  "nombreCategoria": "Pinturas"
}
```

#### Accesorios Eléctricos
```json
{
  "nombreCategoria": "Accesorios Eléctricos"
}
```

#### Cemento y Adhesivos
```json
{
  "nombreCategoria": "Cemento y Adhesivos"
}
```

---

### 2️⃣ CREAR USUARIOS

**Método:** POST  
**URL:** `http://localhost:8080/api/v1/usuarios`  
**Headers:** `Content-Type: application/json`

#### Cliente
```json
{
  "nombre": "Juan Pérez",
  "email": "juan.perez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "cliente"
}
```

#### Vendedor
```json
{
  "nombre": "María García",
  "email": "maria.garcia@example.com",
  "contrasena": "password123",
  "tipoUsuario": "vendedor"
}
```

#### Bodeguero
```json
{
  "nombre": "Carlos López",
  "email": "carlos.lopez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "bodeguero"
}
```

#### Contador
```json
{
  "nombre": "Ana Rodríguez",
  "email": "ana.rodriguez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "contador"
}
```

#### Administrador
```json
{
  "nombre": "Admin Sistema",
  "email": "admin@example.com",
  "contrasena": "admin123",
  "tipoUsuario": "administrador"
}
```

---

### 3️⃣ CREAR PRODUCTOS

**Método:** POST  
**URL:** `http://localhost:8080/api/v1/productos`  
**Headers:** `Content-Type: application/json`

⚠️ **Nota:** Los productos necesitan un `categoriaId` válido. Primero crea las categorías y anota sus IDs.

#### Pernos de Acero (Categoría 1)
```json
{
  "nombreProducto": "Pernos de Acero Inoxidable",
  "marca": "FERMAX",
  "descripcion": "Pernos de alta resistencia para construcción, acero inoxidable 304",
  "precio": 15.99,
  "stock": 500,
  "unidadMedida": "caja de 100",
  "codigoSku": "PERNO-001",
  "categoriaId": 1
}
```

#### Tubería PVC (Categoría 2)
```json
{
  "nombreProducto": "Tubería PVC 1 pulgada",
  "marca": "TIGRE",
  "descripcion": "Tubería de PVC para agua fría, 1 pulgada de diámetro, rollo de 50 metros",
  "precio": 45.50,
  "stock": 120,
  "unidadMedida": "rollo",
  "codigoSku": "TUBERIA-PVC-001",
  "categoriaId": 2
}
```

#### Pintura Blanca (Categoría 3)
```json
{
  "nombreProducto": "Pintura Acrílica Interior Blanco",
  "marca": "SOWAY",
  "descripcion": "Pintura acrílica de alta cobertura para interiores, color blanco, acabado mate",
  "precio": 22.75,
  "stock": 250,
  "unidadMedida": "galón",
  "codigoSku": "PINTURA-BLANCO-001",
  "categoriaId": 3
}
```

#### Cable Eléctrico (Categoría 4)
```json
{
  "nombreProducto": "Cable Eléctrico #14",
  "marca": "INDECO",
  "descripcion": "Cable eléctrico calibre 14 AWG, cobre puro, para instalaciones residenciales",
  "precio": 0.85,
  "stock": 1000,
  "unidadMedida": "metro",
  "codigoSku": "CABLE-14-001",
  "categoriaId": 4
}
```

#### Cemento (Categoría 5)
```json
{
  "nombreProducto": "Cemento Pórtland Gris",
  "marca": "ADELCA",
  "descripcion": "Cemento pórtland gris de alta resistencia, bolsa de 50 kg",
  "precio": 8.50,
  "stock": 800,
  "unidadMedida": "bolsa",
  "codigoSku": "CEMENTO-001",
  "categoriaId": 5
}
```

---

### 4️⃣ CREAR FACTURACIÓN

**Método:** POST  
**URL:** `http://localhost:8080/api/v1/facturacion-clientes`  
**Headers:** `Content-Type: application/json`

⚠️ **Nota:** Necesita un `usuarioId` válido (crea usuarios primero)

```json
{
  "usuarioId": 1,
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellidos": "Pérez García",
  "telefono": "+56912345678",
  "direccion": "Calle Principal 123, Apto 4B",
  "comuna": "Santiago"
}
```

---

### 5️⃣ CREAR INVENTARIOS

**Método:** POST  
**URL:** `http://localhost:8080/api/v1/inventarios`  
**Headers:** `Content-Type: application/json`

⚠️ **Nota:** Necesita un `productoId` válido (crea productos primero)

```json
{
  "productoId": 1,
  "stockActual": 500,
  "stockMinimo": 100,
  "ubicacionBodega": "A-1-01"
}
```

---

## 🌍 PETICIONES GET (Listar datos)

### GET Categorías
```
GET http://localhost:8080/api/v1/categorias
```

### GET Usuarios
```
GET http://localhost:8080/api/v1/usuarios
```

### GET Productos
```
GET http://localhost:8080/api/v1/productos
```

### GET Inventarios
```
GET http://localhost:8080/api/v1/inventarios
```

### GET por ID
```
GET http://localhost:8080/api/v1/categorias/1
GET http://localhost:8080/api/v1/usuarios/1
GET http://localhost:8080/api/v1/productos/1
```

---

## 🔄 ORDEN RECOMENDADO DE CREACIÓN

1. **Categorías** (no dependen de nada)
2. **Usuarios** (no dependen de nada)
3. **Productos** (necesitan categoriaId)
4. **Facturación** (necesita usuarioId)
5. **Inventarios** (necesita productoId)

---

## 🚀 EJEMPLO EN POSTMAN

1. Crea una nueva request
2. Selecciona **POST**
3. Pega la URL: `http://localhost:8080/api/v1/categorias`
4. Ve a la pestaña **Body**
5. Selecciona **raw** → **JSON**
6. Copia y pega SOLO esto:
   ```json
   {
     "nombreCategoria": "Herrajes"
   }
   ```
7. Haz clic en **Send**

✅ Deberías recibir un código 201 (Created)

---

## ❌ ERRORES COMUNES

| Error | Causa | Solución |
|-------|-------|----------|
| `Validation failed... nombreCategoria: rejected value [null]` | El campo `nombreCategoria` está vacío o no existe | Verifica que el JSON tenga exactamente: `{"nombreCategoria": "valor"}` |
| `404 Not Found` | La URL es incorrecta | Verifica que sea: `/api/v1/categorias` (no `/api/v1/categories`) |
| `400 Bad Request` | JSON malformado | Valida el JSON en https://jsonlint.com/ |
| `Content-Type` error | Headers incorrectos | Asegúrate de tener `Content-Type: application/json` |
| `Foreign key constraint failed` | El ID referenciado no existe | Verifica que el categoriaId/usuarioId/productoId exista antes de crear |

---

## 💡 TIPS

- Usa una herramienta como **Postman** o **Insomnia** para testing
- Guarda los IDs que genera la BD después de cada creación
- El campo `id` se genera automáticamente, **NO lo incluyas en el POST**
- Todos los campos con validaciones (@NotBlank, @NotNull) son obligatorios
- Las contraseñas se guardan en texto plano (en producción usar encriptación)
