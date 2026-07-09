import axios from 'axios';
import type {
  CategoryFormState,
  CategoryItem,
<<<<<<< HEAD
  BoletaPedido,
=======
  AuditLog,
  AuditLogRequest,
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517
  Cliente,
  ClienteFormState,
  InventoryFormState,
  InventoryItem,
  LoginRequest,
  OrderDetailItem,
  OrderItem,
  Product,
  ProductFormState,
  ProductImage,
  ProductImageFormState,
  SaleRequest,
  SaleResponse,
  SessionUser,
  TransbankResponse,
  Trabajador,
  TrabajadorFormState,
} from './types';

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const client = axios.create({
  baseURL: apiBaseUrl,
});

export function getApiErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === 'string') return data;
    if (data && typeof data === 'object') {
      const record = data as Record<string, unknown>;
      return String(record.causa ?? record.message ?? record.error ?? error.message);
    }
    return error.message;
  }

  return error instanceof Error ? error.message : 'Error inesperado.';
}

function toNumber(value: string) {
  return value.trim() === '' ? null : Number(value);
}

export const api = {
  async login(payload: LoginRequest): Promise<SessionUser> {
    const { data } = await client.post('/api/v1/auth/login', payload);
    return data;
  },

  async getProducts(): Promise<Product[]> {
    const { data } = await client.get('/api/v1/productos');
    return Array.isArray(data) ? data : [];
  },

  async createProduct(form: ProductFormState): Promise<void> {
    await client.post('/api/v1/productos', {
      nombreProducto: form.nombreProducto,
      marca: form.marca || null,
      descripcion: form.descripcion || null,
      precio: Number(form.precio),
      unidadMedida: form.unidadMedida || null,
      codigoSku: form.codigoSku || null,
      categoriaId: toNumber(form.categoriaId),
      proveedorId: toNumber(form.proveedorId),
    });
  },

  async updateProduct(id: number, form: ProductFormState): Promise<void> {
    await client.put(`/api/v1/productos/${id}`, {
      nombreProducto: form.nombreProducto,
      marca: form.marca || null,
      descripcion: form.descripcion || null,
      precio: Number(form.precio),
      unidadMedida: form.unidadMedida || null,
      codigoSku: form.codigoSku || null,
      categoriaId: toNumber(form.categoriaId),
      proveedorId: toNumber(form.proveedorId),
    });
  },

  async deleteProduct(id: number): Promise<void> {
    await client.delete(`/api/v1/productos/${id}`);
  },

  async getProductImages(): Promise<ProductImage[]> {
    const { data } = await client.get('/api/v1/productos-imagenes');
    return Array.isArray(data) ? data : [];
  },

  async createProductImage(form: ProductImageFormState): Promise<void> {
    await client.post('/api/v1/productos-imagenes', {
      productoId: Number(form.productoId),
      urlImagen: form.urlImagen,
      textoAlternativo: form.textoAlternativo || null,
      principal: form.principal,
    });
  },

  async updateProductImage(id: number, form: ProductImageFormState): Promise<void> {
    await client.put(`/api/v1/productos-imagenes/${id}`, {
      productoId: Number(form.productoId),
      urlImagen: form.urlImagen,
      textoAlternativo: form.textoAlternativo || null,
      principal: form.principal,
    });
  },

  async deleteProductImage(id: number): Promise<void> {
    await client.delete(`/api/v1/productos-imagenes/${id}`);
  },

  async getInventories(): Promise<InventoryItem[]> {
    const { data } = await client.get('/api/v1/inventarios');
    return Array.isArray(data) ? data : [];
  },

  async createInventory(form: InventoryFormState): Promise<void> {
    await client.post('/api/v1/inventarios', {
      productoId: Number(form.productoId),
      proveedorId: toNumber(form.proveedorId),
      stockActual: Number(form.stockActual),
      stockMinimo: Number(form.stockMinimo),
      ubicacionBodega: form.ubicacionBodega || null,
      sucursal: form.sucursal || null,
    });
  },

  async updateInventory(id: number, form: InventoryFormState): Promise<void> {
    await client.put(`/api/v1/inventarios/${id}`, {
      productoId: Number(form.productoId),
      proveedorId: toNumber(form.proveedorId),
      stockActual: Number(form.stockActual),
      stockMinimo: Number(form.stockMinimo),
      ubicacionBodega: form.ubicacionBodega || null,
      sucursal: form.sucursal || null,
    });
  },

  async deleteInventory(id: number): Promise<void> {
    await client.delete(`/api/v1/inventarios/${id}`);
  },

  async getClientes(): Promise<Cliente[]> {
    const { data } = await client.get('/api/v1/clientes');
    return Array.isArray(data) ? data : [];
  },

  async createCliente(form: ClienteFormState): Promise<void> {
    await client.post('/api/v1/clientes', form);
  },

  async updateCliente(id: number, form: ClienteFormState): Promise<void> {
    await client.put(`/api/v1/clientes/${id}`, form);
  },

  async deleteCliente(id: number): Promise<void> {
    await client.delete(`/api/v1/clientes/${id}`);
  },

  async getTrabajadores(): Promise<Trabajador[]> {
    const { data } = await client.get('/api/v1/trabajadores');
    return Array.isArray(data) ? data : [];
  },

  async createTrabajador(form: TrabajadorFormState): Promise<void> {
    await client.post('/api/v1/trabajadores', form);
  },

  async updateTrabajador(id: number, form: TrabajadorFormState): Promise<void> {
    await client.put(`/api/v1/trabajadores/${id}`, form);
  },

  async deleteTrabajador(id: number): Promise<void> {
    await client.delete(`/api/v1/trabajadores/${id}`);
  },

  async getCategories(): Promise<CategoryItem[]> {
    const { data } = await client.get('/api/v1/categorias');
    return Array.isArray(data) ? data : [];
  },

  async createCategory(form: CategoryFormState): Promise<void> {
    await client.post('/api/v1/categorias', form);
  },

  async updateCategory(id: number, form: CategoryFormState): Promise<void> {
    await client.put(`/api/v1/categorias/${id}`, form);
  },

  async deleteCategory(id: number): Promise<void> {
    await client.delete(`/api/v1/categorias/${id}`);
  },

  async getOrders(): Promise<OrderItem[]> {
    const { data } = await client.get('/api/v1/pedidos');
    return Array.isArray(data) ? data : [];
  },

  async getWarehouseOrders(): Promise<BoletaPedido[]> {
    const { data } = await client.get('/api/v1/pedidos/bodega');
    return Array.isArray(data) ? data : [];
  },

  async getPendingTransfers(): Promise<OrderItem[]> {
    const { data } = await client.get('/api/v1/pedidos/pendientes-transferencia');
    return Array.isArray(data) ? data : [];
  },

  async getAllOrderDetails(): Promise<OrderDetailItem[]> {
    const { data } = await client.get('/api/v1/detalles-pedido');
    return Array.isArray(data) ? data : [];
  },

  async getOrderDetails(orderId: number): Promise<OrderDetailItem[]> {
    const { data } = await client.get(`/api/v1/detalles-pedido/pedido/${orderId}`);
    return Array.isArray(data) ? data : [];
  },

  async createClientSale(payload: SaleRequest): Promise<SaleResponse> {
    const { data } = await client.post('/api/v1/ventas/cliente', payload);
    return data;
  },

  async createSellerSale(payload: SaleRequest): Promise<SaleResponse> {
    const { data } = await client.post('/api/v1/ventas/vendedor', payload);
    return data;
  },

  async getTransbankStatus(pagoId: number, token: string): Promise<TransbankResponse> {
    const { data } = await client.get(`/api/v1/pagos/${pagoId}/transbank/estado/${encodeURIComponent(token)}`);
    return data;
  },

  async updateOrderStatus(id: number, estado: string): Promise<OrderItem> {
    const { data } = await client.put(`/api/v1/pedidos/${id}/estado`, { estado });
    return data;
  },

  async getOrdersByStatus(estado: string): Promise<OrderItem[]> {
    const { data } = await client.get(`/api/v1/pedidos/estado/${encodeURIComponent(estado)}`);
    return Array.isArray(data) ? data : [];
  },

  async getReports(): Promise<Record<string, unknown>> {
    const { data } = await client.get('/api/v1/reportes/operaciones');
    return data ?? {};
  },

  async getAuditLogs(): Promise<AuditLog[]> {
    const { data } = await client.get('/api/v1/audit-logs');
    return Array.isArray(data) ? data : [];
  },

  async createAuditLog(payload: AuditLogRequest): Promise<void> {
    await client.post('/api/v1/audit-logs', payload);
  },
};
