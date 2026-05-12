# GUÍA DE PETICIONES POST - FERRETERÍA API

## 1. CREAR CATEGORÍAS

### POST /api/v1/categorias - Herrajes
```json
{
  "nombreCategoria": "Herrajes"
}
```

### POST /api/v1/categorias - Tuberías
```json
{
  "nombreCategoria": "Tuberías"
}
```

### POST /api/v1/categorias - Pinturas
```json
{
  "nombreCategoria": "Pinturas"
}
```

### POST /api/v1/categorias - Accesorios Eléctricos
```json
{
  "nombreCategoria": "Accesorios Eléctricos"
}
```

### POST /api/v1/categorias - Cemento y Adhesivos
```json
{
  "nombreCategoria": "Cemento y Adhesivos"
}
```

---

## 2. CREAR USUARIOS

### POST /api/v1/usuarios - Cliente
```json
{
  "nombre": "Juan Pérez",
  "email": "juan.perez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "cliente"
}
```

### POST /api/v1/usuarios - Vendedor
```json
{
  "nombre": "María García",
  "email": "maria.garcia@example.com",
  "contrasena": "password123",
  "tipoUsuario": "vendedor"
}
```

### POST /api/v1/usuarios - Bodeguero
```json
{
  "nombre": "Carlos López",
  "email": "carlos.lopez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "bodeguero"
}
```

### POST /api/v1/usuarios - Contador
```json
{
  "nombre": "Ana Rodríguez",
  "email": "ana.rodriguez@example.com",
  "contrasena": "password123",
  "tipoUsuario": "contador"
}
```

### POST /api/v1/usuarios - Administrador
```json
{
  "nombre": "Admin Sistema",
  "email": "admin@example.com",
  "contrasena": "admin123",
  "tipoUsuario": "administrador"
}
```

---

## 3. CREAR PRODUCTOS

### POST /api/v1/productos - Pernos de Acero
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

### POST /api/v1/productos - Tubería PVC
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

### POST /api/v1/productos - Pintura Blanca
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

### POST /api/v1/productos - Cable Eléctrico
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

### POST /api/v1/productos - Cemento
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

## 4. CREAR FACTURACIÓN CLIENTES

### POST /api/v1/facturacion-clientes - Juan Pérez
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

### POST /api/v1/facturacion-clientes - María García
```json
{
  "usuarioId": 2,
  "rut": "98765432-1",
  "nombre": "María",
  "apellidos": "García López",
  "telefono": "+56987654321",
  "direccion": "Avenida Secundaria 456",
  "comuna": "Providencia"
}
```

---

## 5. CREAR INVENTARIOS

### POST /api/v1/inventarios - Pernos
```json
{
  "productoId": 1,
  "stockActual": 500,
  "stockMinimo": 100,
  "ubicacionBodega": "A-1-01"
}
```

### POST /api/v1/inventarios - Tuberías
```json
{
  "productoId": 2,
  "stockActual": 120,
  "stockMinimo": 50,
  "ubicacionBodega": "B-2-03"
}
```

### POST /api/v1/inventarios - Pintura
```json
{
  "productoId": 3,
  "stockActual": 250,
  "stockMinimo": 75,
  "ubicacionBodega": "C-1-05"
}
```

### POST /api/v1/inventarios - Cable
```json
{
  "productoId": 4,
  "stockActual": 1000,
  "stockMinimo": 200,
  "ubicacionBodega": "D-3-02"
}
```

### POST /api/v1/inventarios - Cemento
```json
{
  "productoId": 5,
  "stockActual": 800,
  "stockMinimo": 150,
  "ubicacionBodega": "E-2-01"
}
```

---

## COMANDO CURL EJEMPLO

Para crear una categoría con curl:
```bash
curl -X POST http://localhost:8080/api/v1/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombreCategoria": "Herrajes"}'
```

Para crear un producto con curl:
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{"nombreProducto": "Pernos de Acero Inoxidable","marca": "FERMAX","descripcion": "Pernos de alta resistencia","precio": 15.99,"stock": 500,"unidadMedida": "caja de 100","codigoSku": "PERNO-001","categoriaId": 1}'
```

---

## INSTRUCCIONES EN POSTMAN

1. **Crear Nueva Request**
   - Selecciona el método **POST**
   - Coloca la URL: `http://localhost:8080/api/v1/categorias`
   - Ve a la pestaña **Body**
   - Selecciona **raw** y **JSON**
   - Copia el JSON del apartado anterior
   - Haz clic en **Send**

2. **Headers automáticos**
   - Content-Type: application/json (se configura automáticamente)

3. **Orden recomendado para crear datos:**
   - Primero: Categorías (POST /api/v1/categorias)
   - Segundo: Usuarios (POST /api/v1/usuarios)
   - Tercero: Productos (POST /api/v1/productos) - necesita categoriaId
   - Cuarto: Facturación (POST /api/v1/facturacion-clientes) - necesita usuarioId
   - Quinto: Inventarios (POST /api/v1/inventarios) - necesita productoId
