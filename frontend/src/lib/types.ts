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
  cliente?: Cliente | null;
  trabajador?: Trabajador | null;
  estadoPedido?: OrderStatusItem | null;
  fechaPedido?: string | null;
  total?: number | string | null;
  metodoPago?: string | null;
  tipoEntrega?: string | null;
  direccionEntrega?: string | null;
  comunaEntrega?: string | null;
  sucursalRetiro?: string | null;
  numeroBoleta?: string | null;
  neto?: number | string | null;
  iva?: number | string | null;
  boletaEmitida?: boolean | null;
  fechaBoleta?: string | null;
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
  direccionEntrega?: string | null;
  comunaEntrega?: string | null;
  sucursalRetiro?: string | null;
  items: SaleItem[];
};

export type BoletaItem = {
  productoId?: number | null;
  nombreProducto?: string | null;
  sku?: string | null;
  cantidad: number;
  precioUnitario?: number | string | null;
  subtotal?: number | string | null;
  inventarioId?: number | null;
  origenStock?: string | null;
};

export type BoletaPedido = {
  pedidoId: number;
  numeroBoleta?: string | null;
  fechaPedido?: string | null;
  fechaBoleta?: string | null;
  clienteNombre?: string | null;
  clienteRut?: string | null;
  clienteEmail?: string | null;
  metodoPago?: string | null;
  estadoPedido?: string | null;
  tipoEntrega?: string | null;
  direccionEntrega?: string | null;
  comunaEntrega?: string | null;
  sucursalRetiro?: string | null;
  neto?: number | string | null;
  iva?: number | string | null;
  total?: number | string | null;
  items: BoletaItem[];
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
