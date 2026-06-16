import { FormEvent, useEffect, useMemo, useState } from 'react';
import { api, apiBaseUrl } from './lib/api';
import ConfirmModal from './components/ConfirmModal';
import EditModal from './components/EditModal';
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
} from './lib/types';

type EditKind = 'product' | 'inventory' | 'user' | 'category' | 'status' | 'order';

type EditDraft = ProductFormState | InventoryFormState | UserFormState | CategoryFormState | OrderStatusFormState | OrderFormState;

type EditContext = {
  kind: EditKind;
  id: number;
  title: string;
  draft: EditDraft;
  original: EditDraft;
};

const emptyProductForm: ProductFormState = {
  nombreProducto: '',
  marca: '',
  descripcion: '',
  precio: '',
  unidadMedida: 'unidad',
  codigoSku: '',
  categoriaId: '',
  proveedorId: '',
};

const emptyInventoryForm: InventoryFormState = {
  productoId: '',
  proveedorId: '',
  stockActual: '',
  stockMinimo: '0',
  ubicacionBodega: '',
};

const emptyUserForm: UserFormState = {
  nombre: '',
  email: '',
  contrasena: '',
  tipoUsuario: 'cliente',
};

const emptyCategoryForm: CategoryFormState = {
  nombreCategoria: '',
};

const emptyOrderStatusForm: OrderStatusFormState = {
  nombreEstado: '',
};

const emptyOrderForm: OrderFormState = {
  usuarioId: '',
  estadoId: '',
  fechaPedido: '',
  total: '',
  metodoPago: 'efectivo',
  tipoEntrega: 'retiro_tienda',
};

const paymentMethods = ['efectivo', 'tarjeta', 'transferencia'];
const deliveryTypes = ['retiro_tienda', 'despacho_domicilio'];

const toDateTimeLocalValue = (value?: string | null) => (value ? value.slice(0, 16) : '');

const cloneDraft = <T extends EditDraft>(value: T) => ({ ...value });

const productToDraft = (product: Product): ProductFormState => ({
  nombreProducto: product.nombreProducto ?? '',
  marca: product.marca ?? '',
  descripcion: product.descripcion ?? '',
  precio: product.precio != null ? String(product.precio) : '',
  unidadMedida: product.unidadMedida ?? 'unidad',
  codigoSku: product.codigoSku ?? '',
  categoriaId: product.categoriaId != null ? String(product.categoriaId) : '',
  proveedorId: product.proveedorId != null ? String(product.proveedorId) : '',
});

const inventoryToDraft = (inventory: InventoryItem): InventoryFormState => ({
  productoId: inventory.productoId != null ? String(inventory.productoId) : '',
  proveedorId: inventory.proveedorId != null ? String(inventory.proveedorId) : '',
  stockActual: inventory.stockActual != null ? String(inventory.stockActual) : '',
  stockMinimo: inventory.stockMinimo != null ? String(inventory.stockMinimo) : '0',
  ubicacionBodega: inventory.ubicacionBodega ?? '',
});

const userToDraft = (user: UserItem): UserFormState => ({
  nombre: user.nombre ?? '',
  email: user.email ?? '',
  contrasena: user.contrasena ?? '',
  tipoUsuario: user.tipoUsuario ?? 'cliente',
});

const categoryToDraft = (category: CategoryItem): CategoryFormState => ({
  nombreCategoria: category.nombreCategoria ?? '',
});

const statusToDraft = (status: OrderStatusItem): OrderStatusFormState => ({
  nombreEstado: status.nombreEstado ?? '',
});

const orderToDraft = (order: OrderItem): OrderFormState => ({
  usuarioId: order.usuario?.id != null ? String(order.usuario.id) : '',
  estadoId: order.estadoPedido?.idEstado != null ? String(order.estadoPedido.idEstado) : '',
  fechaPedido: toDateTimeLocalValue(order.fechaPedido),
  total: order.total != null ? String(order.total) : '',
  metodoPago: order.metodoPago ?? 'efectivo',
  tipoEntrega: order.tipoEntrega ?? 'retiro_tienda',
});

const isSameDraft = (a: EditDraft, b: EditDraft) => JSON.stringify(a) === JSON.stringify(b);

export default function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [inventories, setInventories] = useState<InventoryItem[]>([]);
  const [users, setUsers] = useState<UserItem[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [orderStatuses, setOrderStatuses] = useState<OrderStatusItem[]>([]);
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'products' | 'inventory' | 'users' | 'categories' | 'statuses' | 'orders'>('products');
  const [statusMessage, setStatusMessage] = useState<string>('');
  const [productForm, setProductForm] = useState<ProductFormState>(emptyProductForm);
  const [inventoryForm, setInventoryForm] = useState<InventoryFormState>(emptyInventoryForm);
  const [userForm, setUserForm] = useState<UserFormState>(emptyUserForm);
  const [categoryForm, setCategoryForm] = useState<CategoryFormState>(emptyCategoryForm);
  const [orderStatusForm, setOrderStatusForm] = useState<OrderStatusFormState>(emptyOrderStatusForm);
  const [orderForm, setOrderForm] = useState<OrderFormState>(emptyOrderForm);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalMessage, setModalMessage] = useState('');
  const [modalLoading, setModalLoading] = useState(false);
  const [modalConfirmFn, setModalConfirmFn] = useState<(() => Promise<void>) | null>(null);
  const [editContext, setEditContext] = useState<EditContext | null>(null);
  const [editLoading, setEditLoading] = useState(false);

  const backendUrl = useMemo(() => apiBaseUrl, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [productData, inventoryData, userData, categoryData, statusData, orderData] = await Promise.allSettled([
        api.getProducts(),
        api.getInventories(),
        api.getUsers(),
        api.getCategories(),
        api.getOrderStatuses(),
        api.getOrders(),
      ]);

      if (productData.status === 'fulfilled') setProducts(productData.value);
      if (inventoryData.status === 'fulfilled') setInventories(inventoryData.value);
      else setInventories([]);
      if (userData.status === 'fulfilled') setUsers(userData.value);
      if (categoryData.status === 'fulfilled') setCategories(categoryData.value);
      if (statusData.status === 'fulfilled') setOrderStatuses(statusData.value);
      if (orderData.status === 'fulfilled') setOrders(orderData.value);

      const rejected = [productData, inventoryData, userData, categoryData, statusData, orderData].filter((result) => result.status === 'rejected');
      setStatusMessage(
        rejected.length > 0
          ? `Datos cargados parcialmente (${rejected.length} módulo${rejected.length > 1 ? 's' : ''} con error).`
          : 'Datos actualizados correctamente.',
      );
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'No se pudieron cargar los datos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleProductSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando producto...');
    try {
      await api.createProduct(productForm);
      setProductForm(emptyProductForm);
      await loadData();
      setStatusMessage('Producto creado correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear producto.');
    }
  };

  const handleInventorySubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando inventario...');
    try {
      await api.createInventory(inventoryForm);
      setInventoryForm(emptyInventoryForm);
      await loadData();
      setStatusMessage('Inventario creado correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear inventario.');
    }
  };

  const handleUserSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando usuario...');
    try {
      await api.createUser(userForm);
      setUserForm(emptyUserForm);
      await loadData();
      setStatusMessage('Usuario creado correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear usuario.');
    }
  };

  const handleCategorySubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando categoría...');
    try {
      await api.createCategory(categoryForm);
      setCategoryForm(emptyCategoryForm);
      await loadData();
      setStatusMessage('Categoría creada correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear categoría.');
    }
  };

  const handleStatusSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando estado de pedido...');
    try {
      await api.createOrderStatus(orderStatusForm);
      setOrderStatusForm(emptyOrderStatusForm);
      await loadData();
      setStatusMessage('Estado de pedido creado correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear estado de pedido.');
    }
  };

  const handleOrderSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatusMessage('Guardando pedido...');
    try {
      await api.createOrder(orderForm);
      setOrderForm(emptyOrderForm);
      await loadData();
      setStatusMessage('Pedido creado correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al crear pedido.');
    }
  };

  const confirmAndDelete = async (label: string, id: number | string, fn: (id: number) => Promise<void>) => {
    setModalTitle(`Eliminar ${label}`);
    setModalMessage(`¿Quiere eliminar ${label} con ID ${id}? Esta acción es irreversible.`);
    setModalLoading(false);
    setModalConfirmFn(() => async () => {
      setModalLoading(true);
      try {
        await fn(Number(id));
        setModalOpen(false);
        setStatusMessage(`${label} eliminado correctamente.`);
        await loadData();
      } catch (error) {
        setStatusMessage(error instanceof Error ? error.message : `Error al eliminar ${label}.`);
      } finally {
        setModalLoading(false);
      }
    });
    setModalOpen(true);
  };

  const openEditModal = (context: EditContext) => {
    setEditContext(context);
  };

  const closeEditModal = () => {
    if (!editLoading) {
      setEditContext(null);
    }
  };

  const updateEditDraft = (updater: (draft: EditDraft) => EditDraft) => {
    setEditContext((current) => {
      if (!current) return current;
      return {
        ...current,
        draft: updater(current.draft),
      };
    });
  };

  const saveEdit = async () => {
    if (!editContext) return;

    setEditLoading(true);
    try {
      switch (editContext.kind) {
        case 'product':
          await api.updateProduct(editContext.id, editContext.draft as ProductFormState);
          setStatusMessage('Producto actualizado correctamente.');
          break;
        case 'inventory':
          await api.updateInventory(editContext.id, editContext.draft as InventoryFormState);
          setStatusMessage('Inventario actualizado correctamente.');
          break;
        case 'user':
          await api.updateUser(editContext.id, editContext.draft as UserFormState);
          setStatusMessage('Usuario actualizado correctamente.');
          break;
        case 'category':
          await api.updateCategory(editContext.id, editContext.draft as CategoryFormState);
          setStatusMessage('Categoría actualizada correctamente.');
          break;
        case 'status':
          await api.updateOrderStatus(editContext.id, editContext.draft as OrderStatusFormState);
          setStatusMessage('Estado de pedido actualizado correctamente.');
          break;
        case 'order':
          await api.updateOrder(editContext.id, editContext.draft as OrderFormState);
          setStatusMessage('Pedido actualizado correctamente.');
          break;
      }

      setEditContext(null);
      await loadData();
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'Error al actualizar los datos.');
    } finally {
      setEditLoading(false);
    }
  };

  const isEditDirty = editContext ? !isSameDraft(editContext.draft, editContext.original) : false;

  const renderEditFields = () => {
    if (!editContext) return null;

    switch (editContext.kind) {
      case 'product': {
        const draft = editContext.draft as ProductFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label>
              Nombre
              <input value={draft.nombreProducto} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), nombreProducto: event.target.value }))} />
            </label>
            <label>
              Marca
              <input value={draft.marca} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), marca: event.target.value }))} />
            </label>
            <label className="field-span-2">
              Descripción
              <textarea value={draft.descripcion} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), descripcion: event.target.value }))} />
            </label>
            <label>
              Precio
              <input type="number" min="0" step="0.01" value={draft.precio} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), precio: event.target.value }))} />
            </label>
            <label>
              Unidad de medida
              <input value={draft.unidadMedida} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), unidadMedida: event.target.value }))} />
            </label>
            <label>
              SKU
              <input value={draft.codigoSku} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), codigoSku: event.target.value }))} />
            </label>
            <label>
              Categoría ID
              <input type="number" min="1" value={draft.categoriaId} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), categoriaId: event.target.value }))} />
            </label>
            <label>
              Proveedor ID
              <input type="number" min="1" value={draft.proveedorId} onChange={(event) => updateEditDraft((current) => ({ ...(current as ProductFormState), proveedorId: event.target.value }))} />
            </label>
          </>
        );
      }
      case 'inventory': {
        const draft = editContext.draft as InventoryFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label>
              Producto ID
              <input type="number" min="1" value={draft.productoId} onChange={(event) => updateEditDraft((current) => ({ ...(current as InventoryFormState), productoId: event.target.value }))} />
            </label>
            <label>
              Proveedor ID
              <input type="number" min="1" value={draft.proveedorId} onChange={(event) => updateEditDraft((current) => ({ ...(current as InventoryFormState), proveedorId: event.target.value }))} />
            </label>
            <label>
              Stock actual
              <input type="number" min="0" value={draft.stockActual} onChange={(event) => updateEditDraft((current) => ({ ...(current as InventoryFormState), stockActual: event.target.value }))} />
            </label>
            <label>
              Stock mínimo
              <input type="number" min="0" value={draft.stockMinimo} onChange={(event) => updateEditDraft((current) => ({ ...(current as InventoryFormState), stockMinimo: event.target.value }))} />
            </label>
            <label className="field-span-2">
              Ubicación bodega
              <input value={draft.ubicacionBodega} onChange={(event) => updateEditDraft((current) => ({ ...(current as InventoryFormState), ubicacionBodega: event.target.value }))} />
            </label>
          </>
        );
      }
      case 'user': {
        const draft = editContext.draft as UserFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label>
              Nombre
              <input value={draft.nombre} onChange={(event) => updateEditDraft((current) => ({ ...(current as UserFormState), nombre: event.target.value }))} />
            </label>
            <label>
              Email
              <input type="email" value={draft.email} onChange={(event) => updateEditDraft((current) => ({ ...(current as UserFormState), email: event.target.value }))} />
            </label>
            <label>
              Contraseña
              <input type="password" value={draft.contrasena} onChange={(event) => updateEditDraft((current) => ({ ...(current as UserFormState), contrasena: event.target.value }))} />
            </label>
            <label>
              Tipo de usuario
              <input value={draft.tipoUsuario} onChange={(event) => updateEditDraft((current) => ({ ...(current as UserFormState), tipoUsuario: event.target.value }))} />
            </label>
          </>
        );
      }
      case 'category': {
        const draft = editContext.draft as CategoryFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label className="field-span-2">
              Nombre categoría
              <input value={draft.nombreCategoria} onChange={(event) => updateEditDraft((current) => ({ ...(current as CategoryFormState), nombreCategoria: event.target.value }))} />
            </label>
          </>
        );
      }
      case 'status': {
        const draft = editContext.draft as OrderStatusFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label className="field-span-2">
              Nombre estado
              <input value={draft.nombreEstado} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderStatusFormState), nombreEstado: event.target.value }))} />
            </label>
          </>
        );
      }
      case 'order': {
        const draft = editContext.draft as OrderFormState;
        return (
          <>
            <label className="field-span-2">
              ID
              <input value={editContext.id} disabled />
            </label>
            <label>
              Usuario
              <select value={draft.usuarioId} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), usuarioId: event.target.value }))}>
                <option value="">Selecciona usuario</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>{user.nombre}</option>
                ))}
              </select>
            </label>
            <label>
              Estado
              <select value={draft.estadoId} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), estadoId: event.target.value }))}>
                <option value="">Selecciona estado</option>
                {orderStatuses.map((status) => (
                  <option key={status.idEstado} value={status.idEstado}>{status.nombreEstado}</option>
                ))}
              </select>
            </label>
            <label>
              Fecha pedido
              <input type="datetime-local" value={draft.fechaPedido} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), fechaPedido: event.target.value }))} />
            </label>
            <label>
              Total
              <input type="number" min="0" step="0.01" value={draft.total} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), total: event.target.value }))} />
            </label>
            <label>
              Método de pago
              <select value={draft.metodoPago} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), metodoPago: event.target.value }))}>
                {paymentMethods.map((method) => (
                  <option key={method} value={method}>{method}</option>
                ))}
              </select>
            </label>
            <label>
              Tipo de entrega
              <select value={draft.tipoEntrega} onChange={(event) => updateEditDraft((current) => ({ ...(current as OrderFormState), tipoEntrega: event.target.value }))}>
                {deliveryTypes.map((delivery) => (
                  <option key={delivery} value={delivery}>{delivery}</option>
                ))}
              </select>
            </label>
          </>
        );
      }
    }
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">F</div>
          <div>
            <p className="eyebrow">FERREMAS</p>
            <h1>Panel Frontend</h1>
          </div>
        </div>

        <nav className="tabs" aria-label="Navegación principal">
          <button className={activeTab === 'products' ? 'tab active' : 'tab'} onClick={() => setActiveTab('products')}>
            Productos
          </button>
          <button className={activeTab === 'inventory' ? 'tab active' : 'tab'} onClick={() => setActiveTab('inventory')}>
            Inventario
          </button>
          <button className={activeTab === 'users' ? 'tab active' : 'tab'} onClick={() => setActiveTab('users')}>
            Usuarios
          </button>
          <button className={activeTab === 'categories' ? 'tab active' : 'tab'} onClick={() => setActiveTab('categories')}>
            Categorías
          </button>
          <button className={activeTab === 'statuses' ? 'tab active' : 'tab'} onClick={() => setActiveTab('statuses')}>
            Estados
          </button>
          <button className={activeTab === 'orders' ? 'tab active' : 'tab'} onClick={() => setActiveTab('orders')}>
            Pedidos
          </button>
        </nav>

        <div className="sidebar-card">
          <p className="sidebar-label">Backend</p>
          <p>{backendUrl}</p>
        </div>

        <div className="sidebar-card muted">
          <p className="sidebar-label">Estado</p>
          <p>{loading ? 'Cargando datos...' : 'Listo'}</p>
        </div>
      </aside>

      <main className="content">
        <header className="hero">
          <div>
            <p className="eyebrow">React + Vite</p>
            <h2>Gestión comercial FERREMAS</h2>
            <p>
              Interfaz conectada al backend FERREMAS para consumir y crear datos de productos, inventario, usuarios, categorías y pedidos.
            </p>
          </div>
          <button className="refresh-button" onClick={loadData}>
            Refrescar datos
          </button>
        </header>

        {statusMessage ? <div className="status-banner">{statusMessage}</div> : null}

        {activeTab === 'products' ? (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo producto</h3>
              <form onSubmit={handleProductSubmit} className="form-grid">
                <input required placeholder="Nombre producto" value={productForm.nombreProducto} onChange={(e) => setProductForm({ ...productForm, nombreProducto: e.target.value })} />
                <input placeholder="Marca" value={productForm.marca} onChange={(e) => setProductForm({ ...productForm, marca: e.target.value })} />
                <input placeholder="Descripción" value={productForm.descripcion} onChange={(e) => setProductForm({ ...productForm, descripcion: e.target.value })} />
                <input required type="number" min="0" step="0.01" placeholder="Precio" value={productForm.precio} onChange={(e) => setProductForm({ ...productForm, precio: e.target.value })} />
                <input placeholder="Unidad medida" value={productForm.unidadMedida} onChange={(e) => setProductForm({ ...productForm, unidadMedida: e.target.value })} />
                <input placeholder="SKU" value={productForm.codigoSku} onChange={(e) => setProductForm({ ...productForm, codigoSku: e.target.value })} />
                <input type="number" min="1" placeholder="Categoria ID" value={productForm.categoriaId} onChange={(e) => setProductForm({ ...productForm, categoriaId: e.target.value })} />
                <input type="number" min="1" placeholder="Proveedor ID" value={productForm.proveedorId} onChange={(e) => setProductForm({ ...productForm, proveedorId: e.target.value })} />
                <button className="primary-button" type="submit">Crear producto</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Productos</h3>
                <span>{products.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th>Marca</th>
                      <th>Precio</th>
                      <th>Categoría</th>
                      <th>Proveedor</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {products.map((product) => (
                      <tr key={product.id}>
                        <td>{product.id}</td>
                        <td>{product.nombreProducto}</td>
                        <td>{product.marca ?? '-'}</td>
                        <td>{product.precio}</td>
                        <td>{product.categoriaNombre ?? '-'}</td>
                        <td>{product.proveedorNombre ?? '-'}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'product',
                                id: product.id,
                                title: `Editar producto #${product.id}`,
                                draft: cloneDraft(productToDraft(product)),
                                original: cloneDraft(productToDraft(product)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`el producto "${product.nombreProducto}"`, product.id, api.deleteProduct)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        ) : activeTab === 'inventory' ? (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo inventario</h3>
              <form onSubmit={handleInventorySubmit} className="form-grid">
                <input required type="number" min="1" placeholder="Producto ID" value={inventoryForm.productoId} onChange={(e) => setInventoryForm({ ...inventoryForm, productoId: e.target.value })} />
                <input type="number" min="1" placeholder="Proveedor ID" value={inventoryForm.proveedorId} onChange={(e) => setInventoryForm({ ...inventoryForm, proveedorId: e.target.value })} />
                <input required type="number" min="0" placeholder="Stock actual" value={inventoryForm.stockActual} onChange={(e) => setInventoryForm({ ...inventoryForm, stockActual: e.target.value })} />
                <input type="number" min="0" placeholder="Stock mínimo" value={inventoryForm.stockMinimo} onChange={(e) => setInventoryForm({ ...inventoryForm, stockMinimo: e.target.value })} />
                <input placeholder="Ubicación bodega" value={inventoryForm.ubicacionBodega} onChange={(e) => setInventoryForm({ ...inventoryForm, ubicacionBodega: e.target.value })} />
                <button className="primary-button" type="submit">Crear inventario</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Inventarios</h3>
                <span>{inventories.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Producto</th>
                      <th>Proveedor</th>
                      <th>Stock actual</th>
                      <th>Stock mínimo</th>
                      <th>Ubicación</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {inventories.map((item) => (
                      <tr key={item.idInventario}>
                        <td>{item.idInventario}</td>
                        <td>{item.nombreProducto ?? item.productoId}</td>
                        <td>{item.nombreProveedor ?? item.proveedorId ?? '-'}</td>
                        <td>{item.stockActual}</td>
                        <td>{item.stockMinimo}</td>
                        <td>{item.ubicacionBodega ?? '-'}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'inventory',
                                id: item.idInventario,
                                title: `Editar inventario #${item.idInventario}`,
                                draft: cloneDraft(inventoryToDraft(item)),
                                original: cloneDraft(inventoryToDraft(item)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`el producto "${item.nombreProducto ?? item.productoId}" del inventario`, item.idInventario, api.deleteInventory)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        ) : activeTab === 'users' ? (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo usuario</h3>
              <form onSubmit={handleUserSubmit} className="form-grid">
                <input required placeholder="Nombre" value={userForm.nombre} onChange={(e) => setUserForm({ ...userForm, nombre: e.target.value })} />
                <input required type="email" placeholder="Email" value={userForm.email} onChange={(e) => setUserForm({ ...userForm, email: e.target.value })} />
                <input required type="password" placeholder="Contraseña" value={userForm.contrasena} onChange={(e) => setUserForm({ ...userForm, contrasena: e.target.value })} />
                <input required placeholder="Tipo de usuario" value={userForm.tipoUsuario} onChange={(e) => setUserForm({ ...userForm, tipoUsuario: e.target.value })} />
                <button className="primary-button" type="submit">Crear usuario</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Usuarios</h3>
                <span>{users.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th>Email</th>
                      <th>Tipo</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((user) => (
                      <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.nombre}</td>
                        <td>{user.email}</td>
                        <td>{user.tipoUsuario}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'user',
                                id: user.id,
                                title: `Editar usuario #${user.id}`,
                                draft: cloneDraft(userToDraft(user)),
                                original: cloneDraft(userToDraft(user)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`el usuario "${user.nombre}"`, user.id, api.deleteUser)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        ) : activeTab === 'categories' ? (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nueva categoría</h3>
              <form onSubmit={handleCategorySubmit} className="form-grid">
                <input required placeholder="Nombre categoría" value={categoryForm.nombreCategoria} onChange={(e) => setCategoryForm({ nombreCategoria: e.target.value })} />
                <button className="primary-button" type="submit">Crear categoría</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Categorías</h3>
                <span>{categories.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map((category) => (
                      <tr key={category.id}>
                        <td>{category.id}</td>
                        <td>{category.nombreCategoria}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'category',
                                id: category.id,
                                title: `Editar categoría #${category.id}`,
                                draft: cloneDraft(categoryToDraft(category)),
                                original: cloneDraft(categoryToDraft(category)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`la categoría "${category.nombreCategoria}"`, category.id, api.deleteCategory)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        ) : activeTab === 'statuses' ? (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo estado de pedido</h3>
              <form onSubmit={handleStatusSubmit} className="form-grid">
                <input required placeholder="Nombre estado" value={orderStatusForm.nombreEstado} onChange={(e) => setOrderStatusForm({ nombreEstado: e.target.value })} />
                <button className="primary-button" type="submit">Crear estado</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Estados</h3>
                <span>{orderStatuses.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orderStatuses.map((status) => (
                      <tr key={status.idEstado}>
                        <td>{status.idEstado}</td>
                        <td>{status.nombreEstado}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'status',
                                id: status.idEstado,
                                title: `Editar estado de pedido #${status.idEstado}`,
                                draft: cloneDraft(statusToDraft(status)),
                                original: cloneDraft(statusToDraft(status)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`el estado de pedido "${status.nombreEstado}"`, status.idEstado, api.deleteOrderStatus)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        ) : (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo pedido</h3>
              <form onSubmit={handleOrderSubmit} className="form-grid">
                <select required value={orderForm.usuarioId} onChange={(e) => setOrderForm({ ...orderForm, usuarioId: e.target.value })}>
                  <option value="">Selecciona usuario</option>
                  {users.map((user) => (
                    <option key={user.id} value={user.id}>{user.nombre}</option>
                  ))}
                </select>
                <select required value={orderForm.estadoId} onChange={(e) => setOrderForm({ ...orderForm, estadoId: e.target.value })}>
                  <option value="">Selecciona estado</option>
                  {orderStatuses.map((status) => (
                    <option key={status.idEstado} value={status.idEstado}>{status.nombreEstado}</option>
                  ))}
                </select>
                <input type="datetime-local" value={orderForm.fechaPedido} onChange={(e) => setOrderForm({ ...orderForm, fechaPedido: e.target.value })} />
                <input required type="number" min="0" step="0.01" placeholder="Total" value={orderForm.total} onChange={(e) => setOrderForm({ ...orderForm, total: e.target.value })} />
                <select value={orderForm.metodoPago} onChange={(e) => setOrderForm({ ...orderForm, metodoPago: e.target.value })}>
                  {paymentMethods.map((method) => (
                    <option key={method} value={method}>{method}</option>
                  ))}
                </select>
                <select value={orderForm.tipoEntrega} onChange={(e) => setOrderForm({ ...orderForm, tipoEntrega: e.target.value })}>
                  {deliveryTypes.map((delivery) => (
                    <option key={delivery} value={delivery}>{delivery}</option>
                  ))}
                </select>
                <button className="primary-button" type="submit">Crear pedido</button>
              </form>
            </article>

            <article className="panel-card list-card">
              <div className="card-head">
                <h3>Pedidos</h3>
                <span>{orders.length}</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Usuario</th>
                      <th>Estado</th>
                      <th>Total</th>
                      <th>Pago</th>
                      <th>Entrega</th>
                      <th className="table-actions-header">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orders.map((order) => (
                      <tr key={order.idPedido}>
                        <td>{order.idPedido}</td>
                        <td>{order.usuario?.nombre ?? order.usuario?.id ?? '-'}</td>
                        <td>{order.estadoPedido?.nombreEstado ?? '-'}</td>
                        <td>{order.total ?? '-'}</td>
                        <td>{order.metodoPago ?? '-'}</td>
                        <td>{order.tipoEntrega ?? '-'}</td>
                        <td>
                          <div className="action-cell">
                            <button
                              className="edit-button"
                              type="button"
                              onClick={() => openEditModal({
                                kind: 'order',
                                id: order.idPedido,
                                title: `Editar pedido #${order.idPedido}`,
                                draft: cloneDraft(orderToDraft(order)),
                                original: cloneDraft(orderToDraft(order)),
                              })}
                            >
                              Editar
                            </button>
                            <button className="danger-button" type="button" onClick={() => confirmAndDelete(`el pedido #${order.idPedido}`, order.idPedido, api.deleteOrder)}>Eliminar</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        )}
      </main>
      <ConfirmModal
        open={modalOpen}
        title={modalTitle}
        message={modalMessage}
        loading={modalLoading}
        onConfirm={async () => {
          if (modalConfirmFn) await modalConfirmFn();
        }}
        onCancel={() => setModalOpen(false)}
      />
      <EditModal
        open={editContext !== null}
        title={editContext?.title ?? ''}
        dirty={isEditDirty}
        saving={editLoading}
        onSave={saveEdit}
        onCancel={closeEditModal}
      >
        {renderEditFields()}
      </EditModal>
    </div>
  );
}
