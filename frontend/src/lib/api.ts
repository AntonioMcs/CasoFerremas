import axios from 'axios';
import type {
  CategoryFormState,
  CategoryItem,
  InventoryFormState,
  InventoryItem,
  OrderFormState,
  OrderItem,
  OrderStatusFormState,
  OrderStatusItem,
  Product,
  ProductFormState,
  UserFormState,
  UserItem,
} from './types';

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const client = axios.create({
  baseURL: apiBaseUrl,
});

function toNumber(value: string) {
  return value.trim() === '' ? null : Number(value);
}

export const api = {
  async getProducts(): Promise<Product[]> {
    const { data } = await client.get('/api/v1/productos');
    return data;
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

  async getInventories(): Promise<InventoryItem[]> {
    const { data } = await client.get('/api/v1/inventarios');
    return data;
  },

  async createInventory(form: InventoryFormState): Promise<void> {
    await client.post('/api/v1/inventarios', {
      productoId: Number(form.productoId),
      proveedorId: toNumber(form.proveedorId),
      stockActual: Number(form.stockActual),
      stockMinimo: Number(form.stockMinimo),
      ubicacionBodega: form.ubicacionBodega || null,
    });
  },

  async getUsers(): Promise<UserItem[]> {
    const { data } = await client.get('/api/v1/usuarios');
    return data;
  },

  async createUser(form: UserFormState): Promise<void> {
    await client.post('/api/v1/usuarios', form);
  },

  async getCategories(): Promise<CategoryItem[]> {
    const { data } = await client.get('/api/v1/categorias');
    return data;
  },

  async createCategory(form: CategoryFormState): Promise<void> {
    await client.post('/api/v1/categorias', form);
  },

  async getOrderStatuses(): Promise<OrderStatusItem[]> {
    const { data } = await client.get('/api/v1/estados-pedido');
    return data;
  },

  async createOrderStatus(form: OrderStatusFormState): Promise<void> {
    await client.post('/api/v1/estados-pedido', form);
  },

  async getOrders(): Promise<OrderItem[]> {
    const { data } = await client.get('/api/v1/pedidos');
    return data;
  },

  async createOrder(form: OrderFormState): Promise<void> {
    await client.post('/api/v1/pedidos', {
      usuarioId: Number(form.usuarioId),
      estadoId: Number(form.estadoId),
      fechaPedido: form.fechaPedido || null,
      total: Number(form.total),
      metodoPago: form.metodoPago,
      tipoEntrega: form.tipoEntrega,
    });
  },
};
