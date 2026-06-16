export type Product = {
  id: number;
  nombreProducto: string;
  marca?: string | null;
  descripcion?: string | null;
  precio: number;
  unidadMedida?: string | null;
  codigoSku?: string | null;
  categoriaId?: number | null;
  categoriaNombre?: string | null;
  proveedorId?: number | null;
  proveedorNombre?: string | null;
  fechaRegistro?: string | null;
};

export type ProductFormState = {
  nombreProducto: string;
  marca: string;
  descripcion: string;
  precio: string;
  unidadMedida: string;
  codigoSku: string;
  categoriaId: string;
  proveedorId: string;
};

export type InventoryItem = {
  idInventario: number;
  productoId?: number | null;
  nombreProducto?: string | null;
  proveedorId?: number | null;
  nombreProveedor?: string | null;
  stockActual: number;
  stockMinimo: number;
  ubicacionBodega?: string | null;
};

export type InventoryFormState = {
  productoId: string;
  proveedorId: string;
  stockActual: string;
  stockMinimo: string;
  ubicacionBodega: string;
};

export type UserItem = {
  id: number;
  nombre: string;
  email: string;
  contrasena?: string | null;
  tipoUsuario: string;
  fechaRegistro?: string | null;
};

export type UserFormState = {
  nombre: string;
  email: string;
  contrasena: string;
  tipoUsuario: string;
};

export type CategoryItem = {
  id: number;
  nombreCategoria: string;
  links?: unknown[];
};

export type CategoryFormState = {
  nombreCategoria: string;
};

export type OrderStatusItem = {
  idEstado: number;
  nombreEstado: string;
};

export type OrderStatusFormState = {
  nombreEstado: string;
};

export type OrderItem = {
  idPedido: number;
  usuario?: {
    id: number;
    nombre: string;
    email?: string | null;
    tipoUsuario?: string | null;
  } | null;
  estadoPedido?: OrderStatusItem | null;
  fechaPedido?: string | null;
  total?: number | string | null;
  metodoPago?: string | null;
  tipoEntrega?: string | null;
};

export type OrderFormState = {
  usuarioId: string;
  estadoId: string;
  fechaPedido: string;
  total: string;
  metodoPago: string;
  tipoEntrega: string;
};
