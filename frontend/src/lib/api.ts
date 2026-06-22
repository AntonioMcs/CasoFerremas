import axios from 'axios';
import type {
  CategoryFormState,
  CategoryItem,
  Cliente,
  ClienteFormState,
  InventoryFormState,
  InventoryItem,
  LoginRequest,
  OrderItem,
  Product,
  ProductFormState,
  ProductImage,
  ProductImageFormState,
  SaleRequest,
  SessionUser,
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

  async getClientes(): Promise<Cliente[]> {
    const { data } = await client.get('/api/v1/clientes');
    return Array.isArray(data) ? data : [];
  },

  async createCliente(form: ClienteFormState): Promise<void> {
    await client.post('/api/v1/clientes', form);
  },

  async getTrabajadores(): Promise<Trabajador[]> {
    const { data } = await client.get('/api/v1/trabajadores');
    return Array.isArray(data) ? data : [];
  },

  async createTrabajador(form: TrabajadorFormState): Promise<void> {
    await client.post('/api/v1/trabajadores', form);
  },

  async getCategories(): Promise<CategoryItem[]> {
    const { data } = await client.get('/api/v1/categorias');
    return Array.isArray(data) ? data : [];
  },

  async createCategory(form: CategoryFormState): Promise<void> {
    await client.post('/api/v1/categorias', form);
  },

  async getOrders(): Promise<OrderItem[]> {
    const { data } = await client.get('/api/v1/pedidos');
    return Array.isArray(data) ? data : [];
  },

  async getPendingTransfers(): Promise<OrderItem[]> {
    const { data } = await client.get('/api/v1/pedidos/pendientes-transferencia');
    return Array.isArray(data) ? data : [];
  },

  async createClientSale(payload: SaleRequest): Promise<OrderItem> {
    const { data } = await client.post('/api/v1/ventas/cliente', payload);
    return data;
  },

  async createSellerSale(payload: SaleRequest): Promise<OrderItem> {
    const { data } = await client.post('/api/v1/ventas/vendedor', payload);
    return data;
  },
};
