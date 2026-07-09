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

export type ProductImage = {
  idImagen: number;
  producto?: Product | null;
  urlImagen: string;
  textoAlternativo?: string | null;
  principal: boolean;
};

export type ProductImageFormState = {
  productoId: string;
  urlImagen: string;
  textoAlternativo: string;
  principal: boolean;
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
  sucursal?: string | null;
};

export type InventoryFormState = {
  productoId: string;
  proveedorId: string;
  stockActual: string;
  stockMinimo: string;
  ubicacionBodega: string;
  sucursal: string;
};

export type Cliente = {
  id: number;
  nombre: string;
  email: string;
  contrasena?: string | null;
  rut?: string | null;
  telefono?: string | null;
  direccion?: string | null;
  comuna?: string | null;
  fechaRegistro?: string | null;
};

export type ClienteFormState = {
  nombre: string;
  email: string;
  contrasena: string;
  rut: string;
  telefono: string;
  direccion: string;
  comuna: string;
};

export type Trabajador = {
  id: number;
  nombre: string;
  email: string;
  contrasena?: string | null;
  rol: 'vendedor' | 'bodeguero' | 'contador' | 'admin' | string;
  activo: boolean;
  fechaRegistro?: string | null;
};

export type TrabajadorFormState = {
  nombre: string;
  email: string;
  contrasena: string;
  rol: string;
  activo: boolean;
};

export type CategoryItem = {
  id: number;
  nombreCategoria: string;
};

export type CategoryFormState = {
  nombreCategoria: string;
};

export type OrderStatusItem = {
  idEstado: number;
  nombreEstado: string;
};

export type OrderItem = {
  idPedido: number;
  idCliente?: number | null;
  idUsuario?: number | null;
  idProducto?: number | null;
  cliente?: Cliente | null;
  trabajador?: Trabajador | null;
  estadoPedido?: OrderStatusItem | null;
  producto?: Product | null;
  fechaPedido?: string | null;
  total?: number | string | null;
  metodoPago?: string | null;
  tipoEntrega?: string | null;
  grupoCompraId?: string | null;
  pedidoReferencia?: number | null;
};

export type OrderDetailItem = {
  idDetalle: number;
  pedido?: OrderItem | null;
  producto?: Product | null;
  cantidad: number;
  precioUnitario?: number | string | null;
  subtotal?: number | string | null;
};

export type TransbankResponse = {
  status: string;
  responseCode: string;
  message: string;
  transactionId?: string | null;
  authorizationCode?: string | null;
  token?: string | null;
  url?: string | null;
};

export type SaleResponse = {
  pedido: OrderItem;
  pedidos?: OrderItem[];
  pedidoPrincipalId?: number | null;
  grupoCompraId?: string | null;
  transbankResponse?: TransbankResponse | null;
};

export type SaleItem = {
  productoId: number;
  inventarioId?: number;
  sucursal?: string;
  cantidad: number;
};

export type SaleRequest = {
  clienteId: number;
  trabajadorId?: number | null;
  metodoPago: string;
  tipoEntrega: string;
  items: SaleItem[];
};

export type LoginRequest = {
  email: string;
  contrasena: string;
};

export type SessionUser = {
  id: number;
  nombre: string;
  email: string;
  rol: 'cliente' | 'vendedor' | 'bodeguero' | 'contador' | 'admin' | string;
  tipoUsuario: 'cliente' | 'trabajador' | string;
  comuna?: string | null;
};

export type AuditLog = {
  idLog: number;
  fecha?: string | null;
  tipoUsuario: string;
  idUsuario: number;
  nombreUsuario?: string | null;
  rol?: string | null;
  modulo: string;
  accion: string;
  descripcion?: string | null;
  entidad?: string | null;
  entidadId?: number | null;
  ip?: string | null;
  userAgent?: string | null;
};

export type AuditLogRequest = {
  tipoUsuario: string;
  idUsuario: number;
  nombreUsuario?: string | null;
  rol?: string | null;
  modulo: string;
  accion: string;
  descripcion?: string | null;
  entidad?: string | null;
  entidadId?: number | null;
};
