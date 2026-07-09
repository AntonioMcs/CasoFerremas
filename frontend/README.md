# FERREMAS Frontend

Frontend en React + Vite para consumir el backend Spring Boot del caso FERREMAS.

## Funcionalidad

- Listado de productos
- Creación de productos
- Listado de inventarios
- Creación de inventarios
- UI responsive con estilo tipo panel

## Configuración

1. Copia `.env.example` a `.env`.
2. Ajusta `VITE_API_BASE_URL` si tu backend usa otra URL.

## Ejecutar

```bash
npm install
npm run dev
```

## Notas

- Backend esperado: `http://localhost:8080`
- Endpoints usados:
  - `/api/v1/productos`
  - `/api/v1/inventarios`
