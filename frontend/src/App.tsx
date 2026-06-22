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
type AdminModule = 'productos' | 'inventario' | 'clientes' | 'trabajadores' | 'pedidos' | 'categorias' | 'imagenes';
type AdminAction = 'ver' | 'agregar' | 'modificar' | 'eliminar';
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

const adminModules: Array<{ key: AdminModule; label: string; description: string }> = [
  { key: 'productos', label: 'Productos', description: 'Catalogo, precios, SKU y ficha comercial.' },
  { key: 'inventario', label: 'Inventario', description: 'Stock por bodega, comuna o canal web.' },
  { key: 'clientes', label: 'Clientes', description: 'Cuentas de compra y datos de contacto.' },
  { key: 'trabajadores', label: 'Trabajadores', description: 'Roles internos para vendedor, bodega, contador y admin.' },
  { key: 'pedidos', label: 'Pedidos', description: 'Compras realizadas y estados de pago.' },
  { key: 'categorias', label: 'Categorias', description: 'Rubros usados para ordenar el catalogo.' },
  { key: 'imagenes', label: 'Imagenes', description: 'Galeria visual asociada a productos.' },
];

export default function App() {
  const [session, setSession] = useState<SessionUser | null>(null);
  const [activeView, setActiveView] = useState<View>('cliente');
  const [path, setPath] = useState(() => window.location.pathname);
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
  const [storeSearchDraft, setStoreSearchDraft] = useState('');
  const [storeSearchQuery, setStoreSearchQuery] = useState('');
  const [sellerSearchDraft, setSellerSearchDraft] = useState('');
  const [sellerSearchQuery, setSellerSearchQuery] = useState('');
  const [adminSearchDraft, setAdminSearchDraft] = useState('');
  const [adminSearchQuery, setAdminSearchQuery] = useState('');
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
  const [expandedAdminModule, setExpandedAdminModule] = useState<AdminModule | null>('productos');
  const [adminAction, setAdminAction] = useState<AdminAction>('ver');
  const [editingProductId, setEditingProductId] = useState('');
  const [editingInventoryId, setEditingInventoryId] = useState('');
  const [editingClienteId, setEditingClienteId] = useState('');
  const [editingTrabajadorId, setEditingTrabajadorId] = useState('');
  const [editingCategoryId, setEditingCategoryId] = useState('');
  const [editingImageId, setEditingImageId] = useState('');
  const [productEditForm, setProductEditForm] = useState<ProductFormState>(emptyProductForm);
  const [inventoryEditForm, setInventoryEditForm] = useState<InventoryFormState>(emptyInventoryForm);
  const [clienteEditForm, setClienteEditForm] = useState<ClienteFormState>(emptyClienteForm);
  const [trabajadorEditForm, setTrabajadorEditForm] = useState<TrabajadorFormState>(emptyTrabajadorForm);
  const [categoryEditForm, setCategoryEditForm] = useState<CategoryFormState>({ nombreCategoria: '' });
  const [imageEditForm, setImageEditForm] = useState<ProductImageFormState>(emptyImageForm);

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
    const syncPath = () => setPath(window.location.pathname);
    window.addEventListener('popstate', syncPath);
    return () => window.removeEventListener('popstate', syncPath);
  }, []);

  useEffect(() => {
    if (session?.rol) setActiveView(normalizeView(session.rol));
  }, [session]);

  const branches = useMemo(() => {
    const names = inventories.map((item) => stockPlace(item)).filter(Boolean);
    return Array.from(new Set(names)).sort((a, b) => a.localeCompare(b));
  }, [inventories]);

  const routeProductId = path.match(/^\/producto\/(\d+)/)?.[1];
  const selectedRouteProduct = routeProductId ? products.find((product) => product.id === Number(routeProductId)) : null;

  const visibleProducts = products.filter((product) => {
    const text = `${product.nombreProducto} ${product.marca ?? ''} ${product.codigoSku ?? ''} ${product.categoriaNombre ?? ''}`.toLowerCase();
    const matchesSearch = text.includes(storeSearchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'todos' || String(product.categoriaId) === selectedCategory || product.categoriaNombre === selectedCategory;
    const matchesBranch = selectedBranch === 'todas' || inventoriesForProduct(product.id).some((item) => stockPlace(item) === selectedBranch);
    return matchesSearch && matchesCategory && matchesBranch;
  });

  const sellerVisibleProducts = products.filter((product) => productMatches(product, sellerSearchQuery));
  const adminVisibleProducts = products.filter((product) => productMatches(product, adminSearchQuery));

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

  function productMatches(product: Product, query: string) {
    const normalizedQuery = query.trim().toLowerCase();
    if (!normalizedQuery) return true;
    const text = `${product.nombreProducto} ${product.marca ?? ''} ${product.codigoSku ?? ''} ${product.categoriaNombre ?? ''}`.toLowerCase();
    return text.includes(normalizedQuery);
  }

  function productSuggestions(value: string) {
    const normalizedValue = value.trim().toLowerCase();
    if (normalizedValue.length < 2) return [];
    return products
      .filter((product) => {
        const name = product.nombreProducto.toLowerCase();
        const sku = product.codigoSku?.toLowerCase() ?? '';
        const brand = product.marca?.toLowerCase() ?? '';
        return name.startsWith(normalizedValue) || sku.startsWith(normalizedValue) || brand.startsWith(normalizedValue) || name.includes(normalizedValue);
      })
      .slice(0, 6);
  }

  function goTo(pathname: string) {
    window.history.pushState(null, '', pathname);
    setPath(pathname);
  }

  function productImage(productId: number) {
    const image = images.find((item) => item.producto?.id === productId && item.principal) ?? images.find((item) => item.producto?.id === productId);
    return image?.urlImagen || fallbackImage;
  }

  function productToForm(product: Product): ProductFormState {
    return {
      nombreProducto: product.nombreProducto ?? '',
      marca: product.marca ?? '',
      descripcion: product.descripcion ?? '',
      precio: product.precio != null ? String(product.precio) : '',
      unidadMedida: product.unidadMedida ?? 'unidad',
      codigoSku: product.codigoSku ?? '',
      categoriaId: product.categoriaId != null ? String(product.categoriaId) : '',
      proveedorId: product.proveedorId != null ? String(product.proveedorId) : '',
    };
  }

  function inventoryToForm(item: InventoryItem): InventoryFormState {
    return {
      productoId: item.productoId != null ? String(item.productoId) : '',
      proveedorId: item.proveedorId != null ? String(item.proveedorId) : '',
      stockActual: item.stockActual != null ? String(item.stockActual) : '',
      stockMinimo: item.stockMinimo != null ? String(item.stockMinimo) : '0',
      ubicacionBodega: item.ubicacionBodega ?? '',
      sucursal: item.sucursal ?? '',
    };
  }

  function clienteToForm(cliente: Cliente): ClienteFormState {
    return {
      nombre: cliente.nombre ?? '',
      email: cliente.email ?? '',
      contrasena: cliente.contrasena ?? '',
      rut: cliente.rut ?? '',
      telefono: cliente.telefono ?? '',
      direccion: cliente.direccion ?? '',
      comuna: cliente.comuna ?? '',
    };
  }

  function trabajadorToForm(trabajador: Trabajador): TrabajadorFormState {
    return {
      nombre: trabajador.nombre ?? '',
      email: trabajador.email ?? '',
      contrasena: trabajador.contrasena ?? '',
      rol: trabajador.rol ?? 'VENDEDOR',
      activo: trabajador.activo ?? true,
    };
  }

  function imageToForm(image: ProductImage): ProductImageFormState {
    return {
      productoId: image.producto?.id != null ? String(image.producto.id) : '',
      urlImagen: image.urlImagen ?? '',
      textoAlternativo: image.textoAlternativo ?? '',
      principal: image.principal ?? false,
    };
  }

  function addToCart(cart: CartLine[], setCart: (cart: CartLine[]) => void, product: Product, inventory: InventoryItem) {
    if (inventory.stockActual <= 0) return;
    const existing = cart.find((line) => line.productoId === product.id && line.inventarioId === inventory.idInventario);
    if (existing) {
      if (existing.cantidad >= inventory.stockActual) {
        setStatusMessage(`No hay mas stock disponible para ${product.nombreProducto} en ${stockPlace(inventory)}.`);
        return;
      }
      setCart(cart.map((line) => (
        line.productoId === product.id && line.inventarioId === inventory.idInventario
          ? { ...line, cantidad: line.cantidad + 1 }
          : line
      )));
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

  const submitProductEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingProductId) return;
    await api.updateProduct(Number(editingProductId), productEditForm);
    await loadData();
    setStatusMessage('Producto actualizado correctamente.');
  };

  const submitInventoryEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingInventoryId) return;
    await api.updateInventory(Number(editingInventoryId), inventoryEditForm);
    await loadData();
    setStatusMessage('Inventario actualizado correctamente.');
  };

  const submitClienteEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingClienteId) return;
    await api.updateCliente(Number(editingClienteId), clienteEditForm);
    await loadData();
    setStatusMessage('Cliente actualizado correctamente.');
  };

  const submitTrabajadorEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingTrabajadorId) return;
    await api.updateTrabajador(Number(editingTrabajadorId), trabajadorEditForm);
    await loadData();
    setStatusMessage('Trabajador actualizado correctamente.');
  };

  const submitCategoryEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingCategoryId) return;
    await api.updateCategory(Number(editingCategoryId), categoryEditForm);
    await loadData();
    setStatusMessage('Categoria actualizada correctamente.');
  };

  const submitImageEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editingImageId) return;
    await api.updateProductImage(Number(editingImageId), imageEditForm);
    await loadData();
    setStatusMessage('Imagen actualizada correctamente.');
  };

  async function deleteAdminItem(module: AdminModule, id: number) {
    try {
      if (module === 'productos') await api.deleteProduct(id);
      if (module === 'inventario') await api.deleteInventory(id);
      if (module === 'clientes') await api.deleteCliente(id);
      if (module === 'trabajadores') await api.deleteTrabajador(id);
      if (module === 'categorias') await api.deleteCategory(id);
      if (module === 'imagenes') await api.deleteProductImage(id);
      if (module === 'pedidos') {
        setStatusMessage('Eliminar pedidos requiere endpoint dedicado; no se ejecuto ninguna accion.');
        return;
      }
      await loadData();
      setStatusMessage('Registro eliminado correctamente.');
    } catch (error) {
      setStatusMessage(getApiErrorMessage(error));
    }
  }

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
        <button className={activeView === 'cliente' ? 'active' : ''} type="button" onClick={() => { setActiveView('cliente'); goTo('/'); }}>Tienda</button>
        {allowedViews.filter((view) => view !== 'cliente').map((view) => (
          <button key={view} className={activeView === view ? 'active' : ''} type="button" onClick={() => { setActiveView(view); goTo('/'); }}>
            {roleLabels[view]}
          </button>
        ))}
        <span>Stock total: {totalStock}</span>
        <span>Stock web: {webStock}</span>
        <span>Backend: {backendUrl}</span>
      </nav>

      <main className="store-main">
        {!selectedRouteProduct && (
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
        )}

        <div className="status-banner">{statusMessage}</div>

        {selectedRouteProduct ? renderProductDetail(selectedRouteProduct) : (
          <>
            {visibleView === 'cliente' && renderStorefront()}
            {visibleView === 'vendedor' && renderSeller()}
            {visibleView === 'bodeguero' && renderWarehouse()}
            {visibleView === 'contador' && renderAccounting()}
            {visibleView === 'admin' && renderAdmin()}
          </>
        )}
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
          {renderSearchBox(storeSearchDraft, setStoreSearchDraft, setStoreSearchQuery, 'Buscar producto o SKU')}
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
              <article className="product-card clickable-card" key={product.id} onClick={() => goTo(`/producto/${product.id}`)}>
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
                  <button
                    className="primary-button"
                    type="button"
                    disabled={!selectedInventory || !session}
                    onClick={(event) => {
                      event.stopPropagation();
                      if (selectedInventory) addToCart(clientCart, setClientCart, product, selectedInventory);
                    }}
                  >
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
          {renderSearchBox(sellerSearchDraft, setSellerSearchDraft, setSellerSearchQuery, 'Buscar producto para vender')}
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
              {sellerVisibleProducts.map((product) => {
                const firstInventory = inventoriesForProduct(product.id).find((item) => item.stockActual > 0);
                return (
                  <tr key={product.id} onClick={() => goTo(`/producto/${product.id}`)}>
                    <td>{product.nombreProducto}</td>
                    <td>{money(product.precio)}</td>
                    <td>{inventoriesForProduct(product.id).map((item) => `${isWebStock(item) ? 'Web' : stockPlace(item)}: ${item.stockActual}`).join(' / ') || 'Sin stock'}</td>
                    <td>
                      <button
                        className="edit-button"
                        type="button"
                        disabled={!firstInventory}
                        onClick={(event) => {
                          event.stopPropagation();
                          if (firstInventory) addToCart(sellerCart, setSellerCart, product, firstInventory);
                        }}
                      >
                        Vender
                      </button>
                    </td>
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
        <div className="admin-module-grid">
          {adminModules.map((module) => (
            <article className={`panel-card admin-module-card ${expandedAdminModule === module.key ? 'active' : ''}`} key={module.key}>
              <button
                className="module-main-button"
                type="button"
                onClick={() => {
                  setExpandedAdminModule(expandedAdminModule === module.key ? null : module.key);
                  setAdminAction('ver');
                }}
              >
                <span>{module.label}</span>
                <strong>{adminModuleCount(module.key)}</strong>
              </button>
              <p>{module.description}</p>
              {expandedAdminModule === module.key && (
                <div className="admin-action-row">
                  {(['ver', 'agregar', 'modificar', 'eliminar'] as AdminAction[]).map((action) => (
                    <button
                      key={action}
                      className={adminAction === action ? 'active' : ''}
                      type="button"
                      disabled={module.key === 'pedidos' && (action === 'agregar' || action === 'modificar')}
                      onClick={() => setAdminAction(action)}
                    >
                      {action}
                    </button>
                  ))}
                </div>
              )}
            </article>
          ))}
        </div>

        {expandedAdminModule && renderAdminActionPanel(expandedAdminModule)}
      </section>
    );
  }

  function adminModuleCount(module: AdminModule) {
    const counts: Record<AdminModule, number> = {
      productos: products.length,
      inventario: inventories.length,
      clientes: clientes.length,
      trabajadores: trabajadores.length,
      pedidos: orders.length,
      categorias: categories.length,
      imagenes: images.length,
    };
    return counts[module];
  }

  function renderAdminActionPanel(module: AdminModule) {
    const title = adminModules.find((item) => item.key === module)?.label ?? module;
    return (
      <article className="panel-card admin-action-panel">
        <div className="card-head">
          <h3>{title}: {adminAction}</h3>
          {module === 'productos' && renderSearchBox(adminSearchDraft, setAdminSearchDraft, setAdminSearchQuery, 'Buscar dentro de productos')}
        </div>
        {adminAction === 'ver' && renderAdminView(module)}
        {adminAction === 'agregar' && renderAdminAdd(module)}
        {adminAction === 'modificar' && renderAdminEdit(module)}
        {adminAction === 'eliminar' && renderAdminDelete(module)}
      </article>
    );
  }

  function renderAdminView(module: AdminModule) {
    if (module === 'productos') {
      return renderSimpleTable(['ID', 'Producto', 'SKU', 'Precio', 'Categoria'], adminVisibleProducts.map((product) => [
        product.id,
        product.nombreProducto,
        product.codigoSku ?? '-',
        money(product.precio),
        product.categoriaNombre ?? '-',
      ]));
    }
    if (module === 'inventario') {
      return renderSimpleTable(['ID', 'Producto', 'Bodega/Sucursal', 'Stock', 'Minimo'], inventories.map((item) => [
        item.idInventario,
        item.nombreProducto ?? item.productoId ?? '-',
        stockPlace(item),
        item.stockActual,
        item.stockMinimo,
      ]));
    }
    if (module === 'clientes') {
      return renderSimpleTable(['ID', 'Nombre', 'Email', 'Telefono', 'Comuna'], clientes.map((cliente) => [
        cliente.id,
        cliente.nombre,
        cliente.email,
        cliente.telefono ?? '-',
        cliente.comuna ?? '-',
      ]));
    }
    if (module === 'trabajadores') {
      return renderSimpleTable(['ID', 'Nombre', 'Email', 'Rol', 'Activo'], trabajadores.map((worker) => [
        worker.id,
        worker.nombre,
        worker.email,
        worker.rol,
        worker.activo ? 'Si' : 'No',
      ]));
    }
    if (module === 'pedidos') {
      return renderSimpleTable(['ID', 'Cliente', 'Estado', 'Pago', 'Total'], orders.map((order) => [
        order.idPedido,
        order.cliente?.nombre ?? '-',
        order.estadoPedido?.nombreEstado ?? '-',
        order.metodoPago ?? '-',
        money(order.total),
      ]));
    }
    if (module === 'categorias') {
      return renderSimpleTable(['ID', 'Nombre'], categories.map((category) => [category.id, category.nombreCategoria]));
    }
    return renderSimpleTable(['ID', 'Producto', 'URL', 'Principal'], images.map((image) => [
      image.idImagen,
      image.producto?.nombreProducto ?? image.producto?.id ?? '-',
      image.urlImagen,
      image.principal ? 'Si' : 'No',
    ]));
  }

  function renderAdminAdd(module: AdminModule) {
    if (module === 'productos') return renderProductForm(productForm, setProductForm, submitProduct, 'Crear producto');
    if (module === 'inventario') return renderInventoryForm(inventoryForm, setInventoryForm, submitInventory, 'Guardar inventario');
    if (module === 'clientes') return renderClienteForm(clienteForm, setClienteForm, submitCliente, 'Crear cliente');
    if (module === 'trabajadores') return renderTrabajadorForm(trabajadorForm, setTrabajadorForm, submitTrabajador, 'Crear trabajador');
    if (module === 'categorias') {
      return (
        <form onSubmit={submitCategory} className="form-grid inline-form">
          <input required placeholder="Nombre categoria" value={categoryForm.nombreCategoria} onChange={(event) => setCategoryForm({ nombreCategoria: event.target.value })} />
          <button className="primary-button" type="submit">Crear categoria</button>
        </form>
      );
    }
    if (module === 'imagenes') return renderImageForm(imageForm, setImageForm, submitImage, 'Guardar imagen');
    return <p className="muted-copy">Los pedidos se generan desde ventas o checkout.</p>;
  }

  function renderAdminEdit(module: AdminModule) {
    if (module === 'productos') {
      return (
        <div className="admin-edit-block">
          <select value={editingProductId} onChange={(event) => {
            setEditingProductId(event.target.value);
            const product = products.find((item) => item.id === Number(event.target.value));
            if (product) setProductEditForm(productToForm(product));
          }}>
            <option value="">Selecciona producto</option>
            {products.map((product) => <option key={product.id} value={product.id}>{product.nombreProducto}</option>)}
          </select>
          {editingProductId && renderProductForm(productEditForm, setProductEditForm, submitProductEdit, 'Actualizar producto')}
        </div>
      );
    }
    if (module === 'inventario') {
      return (
        <div className="admin-edit-block">
          <select value={editingInventoryId} onChange={(event) => {
            setEditingInventoryId(event.target.value);
            const item = inventories.find((inventory) => inventory.idInventario === Number(event.target.value));
            if (item) setInventoryEditForm(inventoryToForm(item));
          }}>
            <option value="">Selecciona inventario</option>
            {inventories.map((item) => <option key={item.idInventario} value={item.idInventario}>{item.nombreProducto ?? item.productoId} - {stockPlace(item)}</option>)}
          </select>
          {editingInventoryId && renderInventoryForm(inventoryEditForm, setInventoryEditForm, submitInventoryEdit, 'Actualizar inventario')}
        </div>
      );
    }
    if (module === 'clientes') {
      return (
        <div className="admin-edit-block">
          <select value={editingClienteId} onChange={(event) => {
            setEditingClienteId(event.target.value);
            const cliente = clientes.find((item) => item.id === Number(event.target.value));
            if (cliente) setClienteEditForm(clienteToForm(cliente));
          }}>
            <option value="">Selecciona cliente</option>
            {clientes.map((cliente) => <option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>)}
          </select>
          {editingClienteId && renderClienteForm(clienteEditForm, setClienteEditForm, submitClienteEdit, 'Actualizar cliente')}
        </div>
      );
    }
    if (module === 'trabajadores') {
      return (
        <div className="admin-edit-block">
          <select value={editingTrabajadorId} onChange={(event) => {
            setEditingTrabajadorId(event.target.value);
            const worker = trabajadores.find((item) => item.id === Number(event.target.value));
            if (worker) setTrabajadorEditForm(trabajadorToForm(worker));
          }}>
            <option value="">Selecciona trabajador</option>
            {trabajadores.map((worker) => <option key={worker.id} value={worker.id}>{worker.nombre}</option>)}
          </select>
          {editingTrabajadorId && renderTrabajadorForm(trabajadorEditForm, setTrabajadorEditForm, submitTrabajadorEdit, 'Actualizar trabajador')}
        </div>
      );
    }
    if (module === 'categorias') {
      return (
        <form onSubmit={submitCategoryEdit} className="form-grid inline-form">
          <select required value={editingCategoryId} onChange={(event) => {
            setEditingCategoryId(event.target.value);
            const category = categories.find((item) => item.id === Number(event.target.value));
            setCategoryEditForm({ nombreCategoria: category?.nombreCategoria ?? '' });
          }}>
            <option value="">Selecciona categoria</option>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.nombreCategoria}</option>)}
          </select>
          <input required placeholder="Nombre categoria" value={categoryEditForm.nombreCategoria} onChange={(event) => setCategoryEditForm({ nombreCategoria: event.target.value })} />
          <button className="primary-button" type="submit">Actualizar categoria</button>
        </form>
      );
    }
    if (module === 'imagenes') {
      return (
        <div className="admin-edit-block">
          <select value={editingImageId} onChange={(event) => {
            setEditingImageId(event.target.value);
            const image = images.find((item) => item.idImagen === Number(event.target.value));
            if (image) setImageEditForm(imageToForm(image));
          }}>
            <option value="">Selecciona imagen</option>
            {images.map((image) => <option key={image.idImagen} value={image.idImagen}>{image.producto?.nombreProducto ?? image.urlImagen}</option>)}
          </select>
          {editingImageId && renderImageForm(imageEditForm, setImageEditForm, submitImageEdit, 'Actualizar imagen')}
        </div>
      );
    }
    return <p className="muted-copy">Los pedidos se modifican desde su flujo operativo.</p>;
  }

  function renderAdminDelete(module: AdminModule) {
    const rows: Array<{ id: number; label: string }> =
      module === 'productos' ? products.map((item) => ({ id: item.id, label: item.nombreProducto })) :
      module === 'inventario' ? inventories.map((item) => ({ id: item.idInventario, label: `${item.nombreProducto ?? item.productoId} - ${stockPlace(item)}` })) :
      module === 'clientes' ? clientes.map((item) => ({ id: item.id, label: item.nombre })) :
      module === 'trabajadores' ? trabajadores.map((item) => ({ id: item.id, label: item.nombre })) :
      module === 'pedidos' ? orders.map((item) => ({ id: item.idPedido, label: `Pedido #${item.idPedido}` })) :
      module === 'categorias' ? categories.map((item) => ({ id: item.id, label: item.nombreCategoria })) :
      images.map((item) => ({ id: item.idImagen, label: item.producto?.nombreProducto ?? item.urlImagen }));

    return (
      <div className="delete-list">
        {rows.map((row) => (
          <div className="delete-row" key={row.id}>
            <span>{row.label}</span>
            <button className="danger-button" type="button" onClick={() => deleteAdminItem(module, row.id)}>Eliminar</button>
          </div>
        ))}
      </div>
    );
  }

  function renderSimpleTable(headers: string[], rows: Array<Array<string | number>>) {
    return (
      <div className="table-wrap">
        <table>
          <thead><tr>{headers.map((header) => <th key={header}>{header}</th>)}</tr></thead>
          <tbody>
            {rows.map((row, index) => (
              <tr key={index}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  function renderProductForm(form: ProductFormState, setForm: (value: ProductFormState) => void, onSubmit: (event: FormEvent<HTMLFormElement>) => void, buttonLabel: string) {
    return (
      <form onSubmit={onSubmit} className="form-grid">
        <input required placeholder="Nombre" value={form.nombreProducto} onChange={(event) => setForm({ ...form, nombreProducto: event.target.value })} />
        <input placeholder="Marca" value={form.marca} onChange={(event) => setForm({ ...form, marca: event.target.value })} />
        <input placeholder="Descripcion" value={form.descripcion} onChange={(event) => setForm({ ...form, descripcion: event.target.value })} />
        <input required type="number" min="0" placeholder="Precio" value={form.precio} onChange={(event) => setForm({ ...form, precio: event.target.value })} />
        <input placeholder="SKU" value={form.codigoSku} onChange={(event) => setForm({ ...form, codigoSku: event.target.value })} />
        <input placeholder="Unidad" value={form.unidadMedida} onChange={(event) => setForm({ ...form, unidadMedida: event.target.value })} />
        <input type="number" placeholder="Categoria ID" value={form.categoriaId} onChange={(event) => setForm({ ...form, categoriaId: event.target.value })} />
        <input type="number" placeholder="Proveedor ID" value={form.proveedorId} onChange={(event) => setForm({ ...form, proveedorId: event.target.value })} />
        <button className="primary-button" type="submit">{buttonLabel}</button>
      </form>
    );
  }

  function renderInventoryForm(form: InventoryFormState, setForm: (value: InventoryFormState) => void, onSubmit: (event: FormEvent<HTMLFormElement>) => void, buttonLabel: string) {
    return (
      <form onSubmit={onSubmit} className="form-grid">
        <input required type="number" placeholder="Producto ID" value={form.productoId} onChange={(event) => setForm({ ...form, productoId: event.target.value })} />
        <input placeholder="Comuna, sucursal o Web" value={form.sucursal} onChange={(event) => setForm({ ...form, sucursal: event.target.value })} />
        <input required type="number" min="0" placeholder="Stock actual" value={form.stockActual} onChange={(event) => setForm({ ...form, stockActual: event.target.value })} />
        <input type="number" min="0" placeholder="Stock minimo" value={form.stockMinimo} onChange={(event) => setForm({ ...form, stockMinimo: event.target.value })} />
        <input placeholder="Ubicacion bodega" value={form.ubicacionBodega} onChange={(event) => setForm({ ...form, ubicacionBodega: event.target.value })} />
        <input type="number" placeholder="Proveedor ID" value={form.proveedorId} onChange={(event) => setForm({ ...form, proveedorId: event.target.value })} />
        <button className="primary-button" type="submit">{buttonLabel}</button>
      </form>
    );
  }

  function renderClienteForm(form: ClienteFormState, setForm: (value: ClienteFormState) => void, onSubmit: (event: FormEvent<HTMLFormElement>) => void, buttonLabel: string) {
    return (
      <form onSubmit={onSubmit} className="form-grid">
        <input required placeholder="Nombre" value={form.nombre} onChange={(event) => setForm({ ...form, nombre: event.target.value })} />
        <input required type="email" placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
        <input required type="password" placeholder="Contrasena" value={form.contrasena} onChange={(event) => setForm({ ...form, contrasena: event.target.value })} />
        <input placeholder="RUT" value={form.rut} onChange={(event) => setForm({ ...form, rut: event.target.value })} />
        <input placeholder="Telefono" value={form.telefono} onChange={(event) => setForm({ ...form, telefono: event.target.value })} />
        <input placeholder="Direccion" value={form.direccion} onChange={(event) => setForm({ ...form, direccion: event.target.value })} />
        <input placeholder="Comuna" value={form.comuna} onChange={(event) => setForm({ ...form, comuna: event.target.value })} />
        <button className="primary-button" type="submit">{buttonLabel}</button>
      </form>
    );
  }

  function renderTrabajadorForm(form: TrabajadorFormState, setForm: (value: TrabajadorFormState) => void, onSubmit: (event: FormEvent<HTMLFormElement>) => void, buttonLabel: string) {
    return (
      <form onSubmit={onSubmit} className="form-grid">
        <input required placeholder="Nombre" value={form.nombre} onChange={(event) => setForm({ ...form, nombre: event.target.value })} />
        <input required type="email" placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
        <input required type="password" placeholder="Contrasena" value={form.contrasena} onChange={(event) => setForm({ ...form, contrasena: event.target.value })} />
        <select value={form.rol} onChange={(event) => setForm({ ...form, rol: event.target.value })}>
          <option value="VENDEDOR">Vendedor</option>
          <option value="BODEGUERO">Bodeguero</option>
          <option value="CONTADOR">Contador</option>
          <option value="ADMIN">Admin</option>
        </select>
        <label className="checkbox-row"><input type="checkbox" checked={form.activo} onChange={(event) => setForm({ ...form, activo: event.target.checked })} /> Activo</label>
        <button className="primary-button" type="submit">{buttonLabel}</button>
      </form>
    );
  }

  function renderImageForm(form: ProductImageFormState, setForm: (value: ProductImageFormState) => void, onSubmit: (event: FormEvent<HTMLFormElement>) => void, buttonLabel: string) {
    return (
      <form onSubmit={onSubmit} className="form-grid">
        <input required type="number" placeholder="Producto ID" value={form.productoId} onChange={(event) => setForm({ ...form, productoId: event.target.value })} />
        <input required placeholder="URL imagen" value={form.urlImagen} onChange={(event) => setForm({ ...form, urlImagen: event.target.value })} />
        <input placeholder="Texto alternativo" value={form.textoAlternativo} onChange={(event) => setForm({ ...form, textoAlternativo: event.target.value })} />
        <label className="checkbox-row"><input type="checkbox" checked={form.principal} onChange={(event) => setForm({ ...form, principal: event.target.checked })} /> Principal</label>
        <button className="primary-button" type="submit">{buttonLabel}</button>
      </form>
    );
  }

  function renderProductDetail(product: Product) {
    const productInventories = inventoriesForProduct(product.id);
    const availableInventories = productInventories.filter((item) => item.stockActual > 0);
    const selectedInventory = availableInventories.find((item) => isWebStock(item)) ?? availableInventories[0];

    return (
      <section className="product-detail-page">
        <button className="ghost-button back-button" type="button" onClick={() => goTo('/')}>Volver al catalogo</button>
        <article className="product-detail-hero">
          <div className="detail-image">
            <img src={productImage(product.id)} alt={product.nombreProducto} />
          </div>
          <div className="detail-info">
            <p className="eyebrow">{product.categoriaNombre ?? product.marca ?? 'FERREMAS'}</p>
            <h2>{product.nombreProducto}</h2>
            <p>{product.descripcion ?? 'Producto disponible para compra y venta asistida.'}</p>
            <div className="detail-meta">
              <span>SKU: {product.codigoSku ?? 'No registrado'}</span>
              <span>Marca: {product.marca ?? 'Sin marca'}</span>
              <span>Unidad: {product.unidadMedida ?? 'unidad'}</span>
              <span>Proveedor: {product.proveedorNombre ?? 'Sin proveedor'}</span>
            </div>
            <strong className="detail-price">{money(product.precio)}</strong>
            <button className="primary-button" type="button" disabled={!selectedInventory || !session} onClick={() => selectedInventory && addToCart(clientCart, setClientCart, product, selectedInventory)}>
              Agregar al carro
            </button>
          </div>
        </article>

        <article className="panel-card">
          <div className="card-head">
            <h3>Inventarios por bodega</h3>
            <span>{productInventories.length}</span>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Bodega/Sucursal</th><th>Ubicacion</th><th>Stock actual</th><th>Stock minimo</th><th>Proveedor</th></tr>
              </thead>
              <tbody>
                {productInventories.map((item) => (
                  <tr key={item.idInventario}>
                    <td>{isWebStock(item) ? 'Web' : stockPlace(item)}</td>
                    <td>{item.ubicacionBodega ?? '-'}</td>
                    <td>{item.stockActual}</td>
                    <td>{item.stockMinimo}</td>
                    <td>{item.nombreProveedor ?? item.proveedorId ?? '-'}</td>
                  </tr>
                ))}
                {productInventories.length === 0 && (
                  <tr><td colSpan={5}>No hay inventario registrado para este producto.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </article>
      </section>
    );
  }

  function renderSearchBox(
    draft: string,
    setDraft: (value: string) => void,
    setQuery: (value: string) => void,
    placeholder: string,
  ) {
    const suggestions = productSuggestions(draft);
    return (
      <div className="local-search">
        <input
          value={draft}
          placeholder={placeholder}
          onChange={(event) => setDraft(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              event.preventDefault();
              setQuery(draft);
            }
          }}
        />
        {suggestions.length > 0 && (
          <div className="suggestions-list">
            {suggestions.map((product) => (
              <button
                key={product.id}
                type="button"
                onClick={() => {
                  setDraft(product.nombreProducto);
                  setQuery(product.nombreProducto);
                }}
              >
                <span>{product.nombreProducto}</span>
                <small>{product.codigoSku ?? product.marca ?? 'FERREMAS'}</small>
              </button>
            ))}
          </div>
        )}
      </div>
    );
  }

  function renderCart(cart: CartLine[], setCart: (cart: CartLine[]) => void, total: number, onPay: () => void, title: string) {
    const updateQuantity = (line: CartLine, nextQuantity: number) => {
      const inventory = inventories.find((item) => item.idInventario === line.inventarioId);
      const maxQuantity = inventory?.stockActual ?? Number.MAX_SAFE_INTEGER;
      const safeQuantity = Math.max(0, Math.min(nextQuantity, maxQuantity));

      if (safeQuantity === 0) {
        setCart(cart.filter((item) => !(item.productoId === line.productoId && item.inventarioId === line.inventarioId)));
        return;
      }

      if (nextQuantity > maxQuantity) {
        setStatusMessage(`Stock maximo disponible: ${maxQuantity} unidades para ${line.nombre}.`);
      }

      setCart(cart.map((item) => (
        item.productoId === line.productoId && item.inventarioId === line.inventarioId
          ? { ...item, cantidad: safeQuantity }
          : item
      )));
    };

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
              <div className="quantity-control">
                <button type="button" onClick={() => updateQuantity(line, line.cantidad - 1)}>-</button>
                <input min="1" type="number" value={line.cantidad} onChange={(event) => updateQuantity(line, Number(event.target.value))} />
                <button type="button" onClick={() => updateQuantity(line, line.cantidad + 1)}>+</button>
              </div>
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
