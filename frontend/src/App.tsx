import { FormEvent, useEffect, useMemo, useState } from 'react';
import { api, apiBaseUrl } from './lib/api';
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
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((user) => (
                      <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.nombre}</td>
                        <td>{user.email}</td>
                        <td>{user.tipoUsuario}</td>
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
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map((category) => (
                      <tr key={category.id}>
                        <td>{category.id}</td>
                        <td>{category.nombreCategoria}</td>
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
                    </tr>
                  </thead>
                  <tbody>
                    {orderStatuses.map((status) => (
                      <tr key={status.idEstado}>
                        <td>{status.idEstado}</td>
                        <td>{status.nombreEstado}</td>
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
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        )}
      </main>
    </div>
  );
}
