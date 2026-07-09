# Manual de API por controlador

Cada archivo describe los endpoints disponibles para un controlador específico.

## Archivos

- `01-Usuarios.md` — Usuarios
- `02-Categorias.md` — Categorías
- `03-Proveedores.md` — Proveedores
- `04-Productos.md` — Productos
- `05-Inventarios.md` — Inventarios
- `06-Pedidos.md` — Pedidos
- `07-Estados-Pedido.md` — Estados de pedido
- `08-Detalle-Pedido.md` — Detalles de pedido
- `09-Carrito-Compra.md` — Carrito de compras
- `10-Despachos.md` — Despachos
- `11-Facturacion-Clientes.md` — Facturación cliente
- `12-Pagos.md` — Pagos y Transbank

## Cómo usar

1. Abrir el archivo del controlador que necesitas.
2. Revisar la ruta base y el método HTTP.
3. Enviar el JSON de ejemplo con la herramienta que prefieras (Postman, curl, etc.).

## Nota

Los endpoints incluyen validaciones y devuelven errores claros con mensajes `⚠️` cuando hay datos inválidos, recursos no encontrados o conflictos de negocio.
