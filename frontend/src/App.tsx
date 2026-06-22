import { FormEvent, useEffect, useMemo, useState } from 'react';
import { api, apiBaseUrl, getApiErrorMessage } from './lib/api';
import type {
  CategoryFormState,
  CategoryItem,
  Cliente,
  ClienteFormState,
  InventoryFormState,
  InventoryItem,
  OrderItem,
  Product,
  ProductFormState,
  ProductImage,
  ProductImageFormState,
  SaleItem,
  SessionUser,
  Trabajador,
  TrabajadorFormState,
} from './lib/types';

type View = 'cliente' | 'vendedor' | 'bodeguero' | 'contador' | 'admin';
type CartLine = Omit<SaleItem, 'sucursal'> & { nombre: string; precio: number; sucursal?: string | null };

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
  sucursal: '',
};

const emptyClienteForm: ClienteFormState = {
  nombre: '',
  email: '',
  contrasena: '',
  rut: '',
  telefono: '',
  direccion: '',
  comuna: '',
};

const emptyTrabajadorForm: TrabajadorFormState = {
  nombre: '',
  email: '',
  contrasena: '',
  rol: 'VENDEDOR',
  activo: true,
};

const emptyImageForm: ProductImageFormState = {
  productoId: '',
  urlImagen: '',
  textoAlternativo: '',
  principal: true,
};

const fallbackImage =
  'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=900&q=80';

const money = (value: number | string | null | undefined) =>
  Number(value ?? 0).toLocaleString('es-CL', { style: 'currency', currency: 'CLP', maximumFractionDigits: 0 });

const roleLabels: Record<View, string> = {
  cliente: 'Tienda',
  vendedor: 'Ventas',
  bodeguero: 'Bodega',
  contador: 'Contabilidad',
  admin: 'Admin',
};

export default function App() {
  const [session, setSession] = useState<SessionUser | null>(null);
  const [activeView, setActiveView] = useState<View>('cliente');
  const [showLogin, setShowLogin] = useState(false);
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [products, setProducts] = useState<Product[]>([]);
  const [inventories, setInventories] = useState<InventoryItem[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([]);
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [pendingTransfers, setPendingTransfers] = useState<OrderItem[]>([]);
  const [images, setImages] = useState<ProductImage[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusMessage, setStatusMessage] = useState('');
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('todos');
  const [selectedBranch, setSelectedBranch] = useState('todas');
  const [clientCart, setClientCart] = useState<CartLine[]>([]);
  const [sellerCart, setSellerCart] = useState<CartLine[]>([]);
  const [selectedClienteId, setSelectedClienteId] = useState('');
  const [selectedSellerClienteId, setSelectedSellerClienteId] = useState('');
  const [selectedVendedorId, setSelectedVendedorId] = useState('');
  const [clientPaymentMethod, setClientPaymentMethod] = useState('tarjeta');
  const [sellerPaymentMethod, setSellerPaymentMethod] = useState('efectivo');
  const [productForm, setProductForm] = useState<ProductFormState>(emptyProductForm);
  const [inventoryForm, setInventoryForm] = useState<InventoryFormState>(emptyInventoryForm);
  const [clienteForm, setClienteForm] = useState<ClienteFormState>(emptyClienteForm);
  const [trabajadorForm, setTrabajadorForm] = useState<TrabajadorFormState>(emptyTrabajadorForm);
  const [imageForm, setImageForm] = useState<ProductImageFormState>(emptyImageForm);
  const [categoryForm, setCategoryForm] = useState<CategoryFormState>({ nombreCategoria: '' });

  const backendUrl = useMemo(() => apiBaseUrl, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [productData, inventoryData, clienteData, trabajadorData, orderData, transferData, imageData, categoryData] =
        await Promise.allSettled([
          api.getProducts(),
          api.getInventories(),
          api.getClientes(),
          api.getTrabajadores(),
          api.getOrders(),
          api.getPendingTransfers(),
          api.getProductImages(),
          api.getCategories(),
        ]);

      if (productData.status === 'fulfilled') setProducts(productData.value);
      if (inventoryData.status === 'fulfilled') setInventories(inventoryData.value);
      if (clienteData.status === 'fulfilled') {
        setClientes(clienteData.value);
        setSelectedClienteId((current) => current || String(clienteData.value[0]?.id ?? ''));
        setSelectedSellerClienteId((current) => current || String(clienteData.value[0]?.id ?? ''));
      }
      if (trabajadorData.status === 'fulfilled') {
        setTrabajadores(trabajadorData.value);
        const vendedor = trabajadorData.value.find((worker) => worker.rol === 'vendedor');
        setSelectedVendedorId((current) => current || String(vendedor?.id ?? trabajadorData.value[0]?.id ?? ''));
      }
      if (orderData.status === 'fulfilled') setOrders(orderData.value);
      if (transferData.status === 'fulfilled') setPendingTransfers(transferData.value);
      if (imageData.status === 'fulfilled') setImages(imageData.value);
      if (categoryData.status === 'fulfilled') setCategories(categoryData.value);

      const rejected = [productData, inventoryData, clienteData, trabajadorData, orderData, transferData, imageData, categoryData]
        .filter((item) => item.status === 'rejected');
      setStatusMessage(rejected.length ? `Datos cargados parcialmente (${rejected.length} modulo(s) con error).` : 'Catalogo actualizado.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'No se pudieron cargar los datos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (session?.rol) setActiveView(normalizeView(session.rol));
  }, [session]);

  const branches = useMemo(() => {
    const names = inventories.map((item) => stockPlace(item)).filter(Boolean);
    return Array.from(new Set(names)).sort((a, b) => a.localeCompare(b));
  }, [inventories]);

  const visibleProducts = products.filter((product) => {
    const text = `${product.nombreProducto} ${product.marca ?? ''} ${product.codigoSku ?? ''} ${product.categoriaNombre ?? ''}`.toLowerCase();
    const matchesSearch = text.includes(search.toLowerCase());
    const matchesCategory = selectedCategory === 'todos' || String(product.categoriaId) === selectedCategory || product.categoriaNombre === selectedCategory;
    const matchesBranch = selectedBranch === 'todas' || inventoriesForProduct(product.id).some((item) => stockPlace(item) === selectedBranch);
    return matchesSearch && matchesCategory && matchesBranch;
  });

  const totalStock = inventories.reduce((sum, item) => sum + (item.stockActual ?? 0), 0);
  const webStock = inventories.filter((item) => isWebStock(item)).reduce((sum, item) => sum + item.stockActual, 0);
  const lowStock = inventories.filter((item) => item.stockActual <= item.stockMinimo);
  const totalSales = orders.reduce((sum, order) => sum + Number(order.total ?? 0), 0);
  const clientTotal = clientCart.reduce((sum, item) => sum + item.precio * item.cantidad, 0);
  const sellerTotal = sellerCart.reduce((sum, item) => sum + item.precio * item.cantidad, 0);
  const visibleView = session ? activeView : 'cliente';

  function normalizeView(rol: string): View {
    const normalizedRole = rol.trim().toLowerCase();
    const roleMap: Record<string, View> = {
      cliente: 'cliente',
      vendedor: 'vendedor',
      cajero: 'vendedor',
      ventas: 'vendedor',
      bodeguero: 'bodeguero',
      bodega: 'bodeguero',
      contador: 'contador',
      contabilidad: 'contador',
      admin: 'admin',
      administrador: 'admin',
      administrativo: 'admin',
    };

    return roleMap[normalizedRole] ?? 'cliente';
  }

  function inventoriesForProduct(productId: number) {
    return inventories.filter((item) => item.productoId === productId);
  }

  function stockPlace(item: InventoryItem) {
    return item.sucursal || item.ubicacionBodega || 'Web';
  }

  function isWebStock(item: InventoryItem) {
    const label = stockPlace(item).toLowerCase();
    return label.includes('web') || label.includes('online');
  }

  function productImage(productId: number) {
    const image = images.find((item) => item.producto?.id === productId && item.principal) ?? images.find((item) => item.producto?.id === productId);
    return image?.urlImagen || fallbackImage;
  }

  function addToCart(cart: CartLine[], setCart: (cart: CartLine[]) => void, product: Product, inventory: InventoryItem) {
    if (inventory.stockActual <= 0) return;
    const existing = cart.find((line) => line.productoId === product.id && line.inventarioId === inventory.idInventario);
    if (existing) {
      setCart(cart.map((line) => (line === existing ? { ...line, cantidad: Math.min(line.cantidad + 1, inventory.stockActual) } : line)));
      return;
    }
    setCart([
      ...cart,
      {
        productoId: product.id,
        inventarioId: inventory.idInventario,
        sucursal: stockPlace(inventory),
        cantidad: 1,
        nombre: product.nombreProducto,
        precio: Number(product.precio),
      },
    ]);
  }

  async function submitLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatusMessage('Validando usuario...');
    try {
      const user = await api.login({ email: loginEmail, contrasena: loginPassword });
      setSession(user);
      setShowLogin(false);
      setLoginPassword('');
      if (user.tipoUsuario === 'cliente') setSelectedClienteId(String(user.id));
      setStatusMessage(`Bienvenido, ${user.nombre}.`);
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'No se pudo iniciar sesion.');
    }
  }

  function logout() {
    setSession(null);
    setActiveView('cliente');
  }

  async function submitSale(kind: 'cliente' | 'vendedor') {
    const cart = kind === 'cliente' ? clientCart : sellerCart;
    const clienteId = kind === 'cliente' ? (session?.tipoUsuario === 'cliente' ? String(session.id) : selectedClienteId) : selectedSellerClienteId;
    const trabajadorId = kind === 'vendedor' ? (session?.tipoUsuario === 'trabajador' ? String(session.id) : selectedVendedorId) : '';
    const metodoPago = kind === 'cliente' ? clientPaymentMethod : sellerPaymentMethod;

    if (!clienteId || cart.length === 0) {
      setStatusMessage('Seleccione cliente y al menos un producto antes de pagar.');
      return;
    }

    const payload = {
      clienteId: Number(clienteId),
      trabajadorId: trabajadorId ? Number(trabajadorId) : null,
      metodoPago,
      tipoEntrega: kind === 'cliente' ? 'despacho_domicilio' : 'retiro_tienda',
      items: cart.map(({ productoId, inventarioId, sucursal, cantidad }) => ({ productoId, inventarioId, sucursal: sucursal ?? undefined, cantidad })),
    };

    try {
      setStatusMessage('Procesando venta y descontando stock...');
      if (kind === 'cliente') {
        await api.createClientSale(payload);
        setClientCart([]);
      } else {
        await api.createSellerSale(payload);
        setSellerCart([]);
      }
      await loadData();
      setStatusMessage('Venta registrada correctamente. Stock actualizado.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'No se pudo registrar la venta.');
    }
  }

  const submitProduct = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await api.createProduct(productForm);
    setProductForm(emptyProductForm);
    await loadData();
  };

  const submitInventory = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await api.createInventory(inventoryForm);
    setInventoryForm(emptyInventoryForm);
    await loadData();
  };

  const submitCliente = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await api.createCliente(clienteForm);
    setClienteForm(emptyClienteForm);
    await loadData();
  };

  const submitTrabajador = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      await api.createTrabajador(trabajadorForm);
      setTrabajadorForm(emptyTrabajadorForm);
      await loadData();
      setStatusMessage('Trabajador creado correctamente.');
    } catch (error) {
      setStatusMessage(getApiErrorMessage(error));
    }
  };

  const submitImage = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await api.createProductImage(imageForm);
    setImageForm(emptyImageForm);
    await loadData();
  };

  const submitCategory = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await api.createCategory(categoryForm);
    setCategoryForm({ nombreCategoria: '' });
    await loadData();
  };

  const allowedViews: View[] = session?.tipoUsuario === 'trabajador' ? [normalizeView(session.rol)] : ['cliente'];

  return (
    <div className="store-shell">
      <header className="store-header">
        <div className="brand-block">
          <div className="brand-mark">FM</div>
          <div>
            <p className="eyebrow">FERREMAS</p>
            <h1>Ferreteria online</h1>
          </div>
        </div>

        <div className="search-box">
          <span>Buscar</span>
          <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Herramientas, pinturas, electricidad, SKU" />
        </div>

        <div className="header-actions">
          <button className="ghost-button" type="button" onClick={loadData}>{loading ? 'Cargando...' : 'Actualizar'}</button>
          {session ? (
            <button className="account-button" type="button" onClick={logout}>
              <span>{session.nombre}</span>
              <strong>Salir</strong>
            </button>
          ) : (
            <button className="account-button" type="button" onClick={() => setShowLogin(true)}>
              <span>Mi cuenta</span>
              <strong>Ingresar</strong>
            </button>
          )}
          <div className="cart-pill">{clientCart.length} items</div>
        </div>
      </header>

      <nav className="category-strip">
        <button className={activeView === 'cliente' ? 'active' : ''} type="button" onClick={() => setActiveView('cliente')}>Tienda</button>
        {allowedViews.filter((view) => view !== 'cliente').map((view) => (
          <button key={view} className={activeView === view ? 'active' : ''} type="button" onClick={() => setActiveView(view)}>
            {roleLabels[view]}
          </button>
        ))}
        <span>Stock total: {totalStock}</span>
        <span>Stock web: {webStock}</span>
        <span>Backend: {backendUrl}</span>
      </nav>

      <main className="store-main">
        <section className="promo-band">
          <div>
            <p className="eyebrow">Catalogo conectado a Supabase</p>
            <h2>Compra herramientas con stock por comuna y web.</h2>
            <p>Elige productos, revisa disponibilidad por sucursal/comuna y paga desde la vista cliente.</p>
          </div>
          <div className="promo-metrics">
            <strong>{products.length}</strong>
            <span>productos</span>
          </div>
        </section>

        <div className="status-banner">{statusMessage}</div>

        {visibleView === 'cliente' && renderStorefront()}
        {visibleView === 'vendedor' && renderSeller()}
        {visibleView === 'bodeguero' && renderWarehouse()}
        {visibleView === 'contador' && renderAccounting()}
        {visibleView === 'admin' && renderAdmin()}
      </main>

      {showLogin && (
        <div className="modal-overlay">
          <article className="login-card">
            <div className="card-head">
              <h3>Ingresar a FERREMAS</h3>
              <button className="close-button" type="button" onClick={() => setShowLogin(false)}>Cerrar</button>
            </div>
            <form className="form-grid" onSubmit={submitLogin}>
              <input required type="email" placeholder="Email registrado" value={loginEmail} onChange={(event) => setLoginEmail(event.target.value)} />
              <input required type="password" placeholder="Contrasena" value={loginPassword} onChange={(event) => setLoginPassword(event.target.value)} />
              <button className="primary-button" type="submit">Entrar</button>
            </form>
          </article>
        </div>
      )}
    </div>
  );

  function renderStorefront() {
    return (
      <section className="shop-layout">
        <aside className="filter-panel">
          <h3>Filtros</h3>
          <label>
            Categoria
            <select value={selectedCategory} onChange={(event) => setSelectedCategory(event.target.value)}>
              <option value="todos">Todas</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>{category.nombreCategoria}</option>
              ))}
            </select>
          </label>
          <label>
            Comuna o web
            <select value={selectedBranch} onChange={(event) => setSelectedBranch(event.target.value)}>
              <option value="todas">Todas</option>
              {branches.map((branch) => <option key={branch} value={branch}>{branch}</option>)}
            </select>
          </label>
          <label>
            Medio de pago
            <select value={clientPaymentMethod} onChange={(event) => setClientPaymentMethod(event.target.value)}>
              <option value="tarjeta">Tarjeta</option>
              <option value="transferencia">Transferencia</option>
              <option value="efectivo">Efectivo</option>
            </select>
          </label>
          {!session && <button className="primary-button full-button" type="button" onClick={() => setShowLogin(true)}>Iniciar sesion para comprar</button>}
        </aside>

        <section className="product-grid">
          {visibleProducts.map((product) => {
            const productInventories = inventoriesForProduct(product.id);
            const availableInventories = productInventories.filter((item) => item.stockActual > 0);
            const selectedInventory = availableInventories.find((item) => isWebStock(item)) ?? availableInventories[0];
            return (
              <article className="product-card" key={product.id}>
                <div className="image-wrap">
                  <img src={productImage(product.id)} alt={product.nombreProducto} />
                  <span>{product.categoriaNombre ?? product.marca ?? 'FERREMAS'}</span>
                </div>
                <div className="product-body">
                  <p className="sku-line">{product.codigoSku ?? 'SKU no registrado'}</p>
                  <h3>{product.nombreProducto}</h3>
                  <p>{product.descripcion ?? 'Producto disponible para compra.'}</p>
                  <strong className="price">{money(product.precio)}</strong>
                  <div className="stock-list">
                    {productInventories.length === 0 ? <span className="stock-empty">Sin stock registrado</span> : productInventories.map((item) => (
                      <span className={isWebStock(item) ? 'stock-web' : ''} key={item.idInventario}>
                        {isWebStock(item) ? 'Web' : stockPlace(item)}: {item.stockActual}
                      </span>
                    ))}
                  </div>
                  <button className="primary-button" type="button" disabled={!selectedInventory || !session} onClick={() => selectedInventory && addToCart(clientCart, setClientCart, product, selectedInventory)}>
                    Agregar al carro
                  </button>
                </div>
              </article>
            );
          })}
        </section>

        {renderCart(clientCart, setClientCart, clientTotal, () => submitSale('cliente'), 'Carro de compra')}
      </section>
    );
  }

  function renderSeller() {
    return (
      <section className="work-layout">
        <div className="toolbar-row">
          <select value={selectedSellerClienteId} onChange={(event) => setSelectedSellerClienteId(event.target.value)}>
            <option value="">Cliente</option>
            {clientes.map((cliente) => <option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>)}
          </select>
          <select value={sellerPaymentMethod} onChange={(event) => setSellerPaymentMethod(event.target.value)}>
            <option value="efectivo">Efectivo</option>
            <option value="tarjeta">Tarjeta</option>
            <option value="transferencia">Transferencia</option>
          </select>
        </div>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Producto</th><th>Precio</th><th>Inventario comuna/web</th><th>Accion</th></tr></thead>
            <tbody>
              {visibleProducts.map((product) => {
                const firstInventory = inventoriesForProduct(product.id).find((item) => item.stockActual > 0);
                return (
                  <tr key={product.id}>
                    <td>{product.nombreProducto}</td>
                    <td>{money(product.precio)}</td>
                    <td>{inventoriesForProduct(product.id).map((item) => `${isWebStock(item) ? 'Web' : stockPlace(item)}: ${item.stockActual}`).join(' / ') || 'Sin stock'}</td>
                    <td><button className="edit-button" type="button" disabled={!firstInventory} onClick={() => firstInventory && addToCart(sellerCart, setSellerCart, product, firstInventory)}>Vender</button></td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        {renderCart(sellerCart, setSellerCart, sellerTotal, () => submitSale('vendedor'), 'Venta asistida')}
      </section>
    );
  }

  function renderWarehouse() {
    return (
      <section className="panel-grid">
        <article className="panel-card form-card">
          <h3>Nuevo inventario</h3>
          <form onSubmit={submitInventory} className="form-grid">
            <input required type="number" placeholder="Producto ID" value={inventoryForm.productoId} onChange={(event) => setInventoryForm({ ...inventoryForm, productoId: event.target.value })} />
            <input placeholder="Comuna, sucursal o Web" value={inventoryForm.sucursal} onChange={(event) => setInventoryForm({ ...inventoryForm, sucursal: event.target.value })} />
            <input required type="number" min="0" placeholder="Stock actual" value={inventoryForm.stockActual} onChange={(event) => setInventoryForm({ ...inventoryForm, stockActual: event.target.value })} />
            <input type="number" min="0" placeholder="Stock minimo" value={inventoryForm.stockMinimo} onChange={(event) => setInventoryForm({ ...inventoryForm, stockMinimo: event.target.value })} />
            <input placeholder="Ubicacion bodega" value={inventoryForm.ubicacionBodega} onChange={(event) => setInventoryForm({ ...inventoryForm, ubicacionBodega: event.target.value })} />
            <input type="number" placeholder="Proveedor ID" value={inventoryForm.proveedorId} onChange={(event) => setInventoryForm({ ...inventoryForm, proveedorId: event.target.value })} />
            <button className="primary-button" type="submit">Guardar inventario</button>
          </form>
        </article>
        <article className="panel-card list-card">
          <div className="card-head"><h3>Inventario por comuna y web</h3><span>{lowStock.length} bajo</span></div>
          <div className="table-wrap">
            <table>
              <thead><tr><th>Producto</th><th>Comuna/Web</th><th>Stock</th><th>Minimo</th><th>Ubicacion</th></tr></thead>
              <tbody>{inventories.map((item) => (
                <tr key={item.idInventario}>
                  <td>{item.nombreProducto ?? item.productoId}</td>
                  <td>{isWebStock(item) ? 'Web' : stockPlace(item)}</td>
                  <td>{item.stockActual}</td>
                  <td>{item.stockMinimo}</td>
                  <td>{item.ubicacionBodega ?? '-'}</td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        </article>
      </section>
    );
  }

  function renderAccounting() {
    return (
      <section className="panel-grid">
        <article className="panel-card metric-card">
          <p className="sidebar-label">Ventas registradas</p>
          <strong>{money(totalSales)}</strong>
          <p className="muted-copy">{orders.length} pedidos totales - {pendingTransfers.length} transferencias pendientes</p>
        </article>
        <article className="panel-card list-card">
          <div className="card-head"><h3>Compras y pendientes</h3><span>{orders.length}</span></div>
          <div className="table-wrap">
            <table>
              <thead><tr><th>ID</th><th>Cliente</th><th>Estado</th><th>Pago</th><th>Total</th><th>Fecha</th></tr></thead>
              <tbody>{orders.map((order) => (
                <tr key={order.idPedido}>
                  <td>{order.idPedido}</td>
                  <td>{order.cliente?.nombre ?? '-'}</td>
                  <td>{order.estadoPedido?.nombreEstado ?? '-'}</td>
                  <td>{order.metodoPago ?? '-'}</td>
                  <td>{money(order.total)}</td>
                  <td>{order.fechaPedido?.slice(0, 10) ?? '-'}</td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        </article>
      </section>
    );
  }

  function renderAdmin() {
    return (
      <section className="admin-stack">
        <div className="stats-row">
          <article className="panel-card metric-card"><p className="sidebar-label">Productos</p><strong>{products.length}</strong></article>
          <article className="panel-card metric-card"><p className="sidebar-label">Clientes</p><strong>{clientes.length}</strong></article>
          <article className="panel-card metric-card"><p className="sidebar-label">Trabajadores</p><strong>{trabajadores.length}</strong></article>
          <article className="panel-card metric-card"><p className="sidebar-label">Pedidos</p><strong>{orders.length}</strong></article>
        </div>

        <section className="panel-grid">
          <article className="panel-card form-card">
            <h3>Nuevo producto</h3>
            <form onSubmit={submitProduct} className="form-grid">
              <input required placeholder="Nombre" value={productForm.nombreProducto} onChange={(event) => setProductForm({ ...productForm, nombreProducto: event.target.value })} />
              <input placeholder="Marca" value={productForm.marca} onChange={(event) => setProductForm({ ...productForm, marca: event.target.value })} />
              <input placeholder="Descripcion" value={productForm.descripcion} onChange={(event) => setProductForm({ ...productForm, descripcion: event.target.value })} />
              <input required type="number" min="0" placeholder="Precio" value={productForm.precio} onChange={(event) => setProductForm({ ...productForm, precio: event.target.value })} />
              <input placeholder="SKU" value={productForm.codigoSku} onChange={(event) => setProductForm({ ...productForm, codigoSku: event.target.value })} />
              <input placeholder="Unidad" value={productForm.unidadMedida} onChange={(event) => setProductForm({ ...productForm, unidadMedida: event.target.value })} />
              <input type="number" placeholder="Categoria ID" value={productForm.categoriaId} onChange={(event) => setProductForm({ ...productForm, categoriaId: event.target.value })} />
              <input type="number" placeholder="Proveedor ID" value={productForm.proveedorId} onChange={(event) => setProductForm({ ...productForm, proveedorId: event.target.value })} />
              <button className="primary-button" type="submit">Crear producto</button>
            </form>
          </article>
          <article className="panel-card form-card">
            <h3>Imagen de producto</h3>
            <form onSubmit={submitImage} className="form-grid">
              <input required type="number" placeholder="Producto ID" value={imageForm.productoId} onChange={(event) => setImageForm({ ...imageForm, productoId: event.target.value })} />
              <input required placeholder="URL imagen" value={imageForm.urlImagen} onChange={(event) => setImageForm({ ...imageForm, urlImagen: event.target.value })} />
              <input placeholder="Texto alternativo" value={imageForm.textoAlternativo} onChange={(event) => setImageForm({ ...imageForm, textoAlternativo: event.target.value })} />
              <label className="checkbox-row"><input type="checkbox" checked={imageForm.principal} onChange={(event) => setImageForm({ ...imageForm, principal: event.target.checked })} /> Principal</label>
              <button className="primary-button" type="submit">Guardar imagen</button>
            </form>
          </article>
        </section>

        <section className="panel-grid">
          <article className="panel-card form-card">
            <h3>Nuevo cliente</h3>
            <form onSubmit={submitCliente} className="form-grid">
              <input required placeholder="Nombre" value={clienteForm.nombre} onChange={(event) => setClienteForm({ ...clienteForm, nombre: event.target.value })} />
              <input required type="email" placeholder="Email" value={clienteForm.email} onChange={(event) => setClienteForm({ ...clienteForm, email: event.target.value })} />
              <input required type="password" placeholder="Contrasena" value={clienteForm.contrasena} onChange={(event) => setClienteForm({ ...clienteForm, contrasena: event.target.value })} />
              <input placeholder="RUT" value={clienteForm.rut} onChange={(event) => setClienteForm({ ...clienteForm, rut: event.target.value })} />
              <input placeholder="Telefono" value={clienteForm.telefono} onChange={(event) => setClienteForm({ ...clienteForm, telefono: event.target.value })} />
              <input placeholder="Direccion" value={clienteForm.direccion} onChange={(event) => setClienteForm({ ...clienteForm, direccion: event.target.value })} />
              <input placeholder="Comuna" value={clienteForm.comuna} onChange={(event) => setClienteForm({ ...clienteForm, comuna: event.target.value })} />
              <button className="primary-button" type="submit">Crear cliente</button>
            </form>
          </article>
          <article className="panel-card form-card">
            <h3>Nuevo trabajador</h3>
            <form onSubmit={submitTrabajador} className="form-grid">
              <input required placeholder="Nombre" value={trabajadorForm.nombre} onChange={(event) => setTrabajadorForm({ ...trabajadorForm, nombre: event.target.value })} />
              <input required type="email" placeholder="Email" value={trabajadorForm.email} onChange={(event) => setTrabajadorForm({ ...trabajadorForm, email: event.target.value })} />
              <input required type="password" placeholder="Contrasena" value={trabajadorForm.contrasena} onChange={(event) => setTrabajadorForm({ ...trabajadorForm, contrasena: event.target.value })} />
              <select value={trabajadorForm.rol} onChange={(event) => setTrabajadorForm({ ...trabajadorForm, rol: event.target.value })}>
                <option value="VENDEDOR">Vendedor</option>
                <option value="BODEGUERO">Bodeguero</option>
                <option value="CONTADOR">Contador</option>
                <option value="ADMIN">Admin</option>
              </select>
              <button className="primary-button" type="submit">Crear trabajador</button>
            </form>
          </article>
        </section>

        <article className="panel-card form-card">
          <h3>Nueva categoria</h3>
          <form onSubmit={submitCategory} className="form-grid inline-form">
            <input required placeholder="Nombre categoria" value={categoryForm.nombreCategoria} onChange={(event) => setCategoryForm({ nombreCategoria: event.target.value })} />
            <button className="primary-button" type="submit">Crear categoria</button>
          </form>
        </article>
      </section>
    );
  }

  function renderCart(cart: CartLine[], setCart: (cart: CartLine[]) => void, total: number, onPay: () => void, title: string) {
    return (
      <article className="panel-card cart-card">
        <div className="card-head">
          <h3>{title}</h3>
          <span>{cart.length}</span>
        </div>
        <div className="cart-lines">
          {cart.length === 0 ? <p className="muted-copy">Sin productos seleccionados.</p> : cart.map((line) => (
            <div className="cart-line" key={`${line.productoId}-${line.inventarioId}`}>
              <div>
                <strong>{line.nombre}</strong>
                <p>{line.sucursal ?? 'Web'} - {money(line.precio)}</p>
              </div>
              <input min="1" type="number" value={line.cantidad} onChange={(event) => setCart(cart.map((item) => (item === line ? { ...item, cantidad: Number(event.target.value) } : item)))} />
            </div>
          ))}
        </div>
        <div className="total-row">
          <span>Total</span>
          <strong>{money(total)}</strong>
        </div>
        <button className="primary-button full-button" type="button" disabled={cart.length === 0} onClick={onPay}>Pagar</button>
      </article>
    );
  }
}
