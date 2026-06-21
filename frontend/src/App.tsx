import { FormEvent, useEffect, useMemo, useState } from 'react';
import { api, apiBaseUrl } from './lib/api';
import type {
  CategoryFormState,
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
  rol: 'vendedor',
  activo: true,
};

const emptyImageForm: ProductImageFormState = {
  productoId: '',
  urlImagen: '',
  textoAlternativo: '',
  principal: true,
};

const money = (value: number | string | null | undefined) =>
  Number(value ?? 0).toLocaleString('es-CL', { style: 'currency', currency: 'CLP', maximumFractionDigits: 0 });

const fallbackImage =
  'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=900&q=80';

export default function App() {
  const [activeView, setActiveView] = useState<View>('cliente');
  const [products, setProducts] = useState<Product[]>([]);
  const [inventories, setInventories] = useState<InventoryItem[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([]);
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [pendingTransfers, setPendingTransfers] = useState<OrderItem[]>([]);
  const [images, setImages] = useState<ProductImage[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusMessage, setStatusMessage] = useState('');
  const [catalogSearch, setCatalogSearch] = useState('');
  const [sellerSearch, setSellerSearch] = useState('');
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
      const [productData, inventoryData, clienteData, trabajadorData, orderData, transferData, imageData] = await Promise.allSettled([
        api.getProducts(),
        api.getInventories(),
        api.getClientes(),
        api.getTrabajadores(),
        api.getOrders(),
        api.getPendingTransfers(),
        api.getProductImages(),
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

      const rejected = [productData, inventoryData, clienteData, trabajadorData, orderData, transferData, imageData].filter((item) => item.status === 'rejected');
      setStatusMessage(rejected.length ? `Datos cargados parcialmente (${rejected.length} modulo(s) con error).` : 'Datos actualizados correctamente.');
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : 'No se pudieron cargar los datos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const visibleCatalog = products.filter((product) =>
    `${product.nombreProducto} ${product.marca ?? ''} ${product.codigoSku ?? ''}`.toLowerCase().includes(catalogSearch.toLowerCase()),
  );

  const visibleSellerProducts = products.filter((product) =>
    `${product.nombreProducto} ${product.marca ?? ''} ${product.codigoSku ?? ''}`.toLowerCase().includes(sellerSearch.toLowerCase()),
  );

  const totalStock = inventories.reduce((sum, item) => sum + (item.stockActual ?? 0), 0);
  const lowStock = inventories.filter((item) => item.stockActual <= item.stockMinimo);
  const totalSales = orders.reduce((sum, order) => sum + Number(order.total ?? 0), 0);
  const clientTotal = clientCart.reduce((sum, item) => sum + item.precio * item.cantidad, 0);
  const sellerTotal = sellerCart.reduce((sum, item) => sum + item.precio * item.cantidad, 0);

  const inventoriesForProduct = (productId: number) => inventories.filter((item) => item.productoId === productId && item.stockActual > 0);
  const productImage = (productId: number) => {
    const image = images.find((item) => item.producto?.id === productId && item.principal) ?? images.find((item) => item.producto?.id === productId);
    return image?.urlImagen || fallbackImage;
  };

  const addToCart = (cart: CartLine[], setCart: (cart: CartLine[]) => void, product: Product, inventory: InventoryItem) => {
    const existing = cart.find((line) => line.productoId === product.id && line.inventarioId === inventory.idInventario);
    if (existing) {
      setCart(cart.map((line) => (line === existing ? { ...line, cantidad: line.cantidad + 1 } : line)));
      return;
    }
    setCart([
      ...cart,
      {
        productoId: product.id,
        inventarioId: inventory.idInventario,
        sucursal: inventory.sucursal,
        cantidad: 1,
        nombre: product.nombreProducto,
        precio: Number(product.precio),
      },
    ]);
  };

  const submitSale = async (kind: 'cliente' | 'vendedor') => {
    const cart = kind === 'cliente' ? clientCart : sellerCart;
    const clienteId = kind === 'cliente' ? selectedClienteId : selectedSellerClienteId;
    const trabajadorId = kind === 'vendedor' ? selectedVendedorId : '';
    const metodoPago = kind === 'cliente' ? clientPaymentMethod : sellerPaymentMethod;

    if (!clienteId || cart.length === 0) {
      setStatusMessage('Seleccione cliente y al menos un producto antes de pagar.');
      return;
    }

    setStatusMessage('Procesando venta y descontando stock...');
    const payload = {
      clienteId: Number(clienteId),
      trabajadorId: trabajadorId ? Number(trabajadorId) : null,
      metodoPago,
      tipoEntrega: kind === 'cliente' ? 'despacho_domicilio' : 'retiro_tienda',
      items: cart.map(({ productoId, inventarioId, sucursal, cantidad }) => ({ productoId, inventarioId, sucursal: sucursal ?? undefined, cantidad })),
    };

    try {
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
  };

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
    await api.createTrabajador(trabajadorForm);
    setTrabajadorForm(emptyTrabajadorForm);
    await loadData();
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

  const renderCart = (cart: CartLine[], setCart: (cart: CartLine[]) => void, total: number, onPay: () => void) => (
    <article className="panel-card sticky-card">
      <div className="card-head">
        <h3>Carrito</h3>
        <span>{cart.length}</span>
      </div>
      <div className="cart-lines">
        {cart.length === 0 ? <p className="muted-copy">Sin productos seleccionados.</p> : cart.map((line) => (
          <div className="cart-line" key={`${line.productoId}-${line.inventarioId}`}>
            <div>
              <strong>{line.nombre}</strong>
              <p>{line.sucursal ?? 'Sucursal sin definir'} · {money(line.precio)}</p>
            </div>
            <input
              min="1"
              type="number"
              value={line.cantidad}
              onChange={(event) => setCart(cart.map((item) => (item === line ? { ...item, cantidad: Number(event.target.value) } : item)))}
            />
          </div>
        ))}
      </div>
      <div className="total-row">
        <span>Total</span>
        <strong>{money(total)}</strong>
      </div>
      <button className="primary-button full-button" type="button" onClick={onPay}>Pagar</button>
    </article>
  );

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">FM</div>
          <div>
            <p className="eyebrow">FERREMAS</p>
            <h1>Operacion comercial</h1>
          </div>
        </div>

        <nav className="tabs">
          {([
            ['cliente', 'Vista cliente'],
            ['vendedor', 'Vista vendedor'],
            ['bodeguero', 'Vista bodeguero'],
            ['contador', 'Vista contador'],
            ['admin', 'Dashboard admin'],
          ] as const).map(([key, label]) => (
            <button key={key} className={`tab ${activeView === key ? 'active' : ''}`} type="button" onClick={() => setActiveView(key)}>
              {label}
            </button>
          ))}
        </nav>

        <div className="sidebar-card">
          <p className="sidebar-label">Backend</p>
          <strong>{backendUrl}</strong>
        </div>
        <div className="sidebar-card muted">
          <p className="sidebar-label">Stock total</p>
          <strong>{totalStock} unidades</strong>
        </div>
      </aside>

      <main className="content">
        <section className="hero">
          <div>
            <p className="eyebrow">{activeView}</p>
            <h2>{activeView === 'cliente' ? 'Catalogo y checkout' : activeView === 'admin' ? 'Dashboard administrativo' : `Modulo ${activeView}`}</h2>
            <p>Productos, stock por sucursal, ventas y pedidos conectados al backend FERREMAS con PostgreSQL/Supabase.</p>
          </div>
          <button className="refresh-button" type="button" onClick={loadData}>{loading ? 'Cargando...' : 'Actualizar'}</button>
        </section>

        <div className="status-banner">{statusMessage}</div>

        {activeView === 'cliente' && (
          <section className="commerce-layout">
            <div>
              <div className="toolbar-row">
                <input placeholder="Buscar producto, marca o SKU" value={catalogSearch} onChange={(event) => setCatalogSearch(event.target.value)} />
                <select value={selectedClienteId} onChange={(event) => setSelectedClienteId(event.target.value)}>
                  <option value="">Cliente</option>
                  {clientes.map((cliente) => <option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>)}
                </select>
                <select value={clientPaymentMethod} onChange={(event) => setClientPaymentMethod(event.target.value)}>
                  <option value="tarjeta">tarjeta</option>
                  <option value="transferencia">transferencia</option>
                  <option value="efectivo">efectivo</option>
                </select>
              </div>
              <div className="product-grid">
                {visibleCatalog.map((product) => {
                  const productInventories = inventoriesForProduct(product.id);
                  const firstInventory = productInventories[0];
                  return (
                    <article className="product-card" key={product.id}>
                      <img src={productImage(product.id)} alt={product.nombreProducto} />
                      <div className="product-body">
                        <p className="eyebrow">{product.marca ?? product.codigoSku ?? 'FERREMAS'}</p>
                        <h3>{product.nombreProducto}</h3>
                        <p>{product.descripcion ?? 'Producto disponible para compra.'}</p>
                        <strong>{money(product.precio)}</strong>
                        <div className="stock-list">
                          {productInventories.length === 0 ? <span>Sin stock registrado</span> : productInventories.map((item) => (
                            <span key={item.idInventario}>{item.sucursal ?? item.ubicacionBodega ?? 'Sucursal'}: {item.stockActual}</span>
                          ))}
                        </div>
                        <button className="primary-button" type="button" disabled={!firstInventory} onClick={() => firstInventory && addToCart(clientCart, setClientCart, product, firstInventory)}>
                          Agregar al carrito
                        </button>
                      </div>
                    </article>
                  );
                })}
              </div>
            </div>
            {renderCart(clientCart, setClientCart, clientTotal, () => submitSale('cliente'))}
          </section>
        )}

        {activeView === 'vendedor' && (
          <section className="commerce-layout">
            <div>
              <div className="toolbar-row">
                <input placeholder="Buscar para vender" value={sellerSearch} onChange={(event) => setSellerSearch(event.target.value)} />
                <select value={selectedSellerClienteId} onChange={(event) => setSelectedSellerClienteId(event.target.value)}>
                  <option value="">Cliente</option>
                  {clientes.map((cliente) => <option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>)}
                </select>
                <select value={selectedVendedorId} onChange={(event) => setSelectedVendedorId(event.target.value)}>
                  <option value="">Vendedor</option>
                  {trabajadores.filter((worker) => worker.rol === 'vendedor').map((worker) => <option key={worker.id} value={worker.id}>{worker.nombre}</option>)}
                </select>
                <select value={sellerPaymentMethod} onChange={(event) => setSellerPaymentMethod(event.target.value)}>
                  <option value="efectivo">efectivo</option>
                  <option value="tarjeta">tarjeta</option>
                  <option value="transferencia">transferencia</option>
                </select>
              </div>
              <article className="panel-card list-card">
                <div className="table-wrap">
                  <table>
                    <thead>
                      <tr><th>Producto</th><th>SKU</th><th>Precio</th><th>Stock por sucursal</th><th>Accion</th></tr>
                    </thead>
                    <tbody>
                      {visibleSellerProducts.map((product) => {
                        const firstInventory = inventoriesForProduct(product.id)[0];
                        return (
                          <tr key={product.id}>
                            <td>{product.nombreProducto}</td>
                            <td>{product.codigoSku ?? '-'}</td>
                            <td>{money(product.precio)}</td>
                            <td>{inventoriesForProduct(product.id).map((item) => `${item.sucursal ?? item.ubicacionBodega ?? 'Sucursal'}: ${item.stockActual}`).join(' / ') || 'Sin stock'}</td>
                            <td><button className="edit-button" type="button" disabled={!firstInventory} onClick={() => firstInventory && addToCart(sellerCart, setSellerCart, product, firstInventory)}>Vender</button></td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </article>
            </div>
            {renderCart(sellerCart, setSellerCart, sellerTotal, () => submitSale('vendedor'))}
          </section>
        )}

        {activeView === 'bodeguero' && (
          <section className="panel-grid">
            <article className="panel-card form-card">
              <h3>Nuevo inventario</h3>
              <form onSubmit={submitInventory} className="form-grid">
                <input required type="number" placeholder="Producto ID" value={inventoryForm.productoId} onChange={(event) => setInventoryForm({ ...inventoryForm, productoId: event.target.value })} />
                <input placeholder="Sucursal" value={inventoryForm.sucursal} onChange={(event) => setInventoryForm({ ...inventoryForm, sucursal: event.target.value })} />
                <input required type="number" min="0" placeholder="Stock actual" value={inventoryForm.stockActual} onChange={(event) => setInventoryForm({ ...inventoryForm, stockActual: event.target.value })} />
                <input type="number" min="0" placeholder="Stock minimo" value={inventoryForm.stockMinimo} onChange={(event) => setInventoryForm({ ...inventoryForm, stockMinimo: event.target.value })} />
                <input placeholder="Ubicacion bodega" value={inventoryForm.ubicacionBodega} onChange={(event) => setInventoryForm({ ...inventoryForm, ubicacionBodega: event.target.value })} />
                <input type="number" placeholder="Proveedor ID" value={inventoryForm.proveedorId} onChange={(event) => setInventoryForm({ ...inventoryForm, proveedorId: event.target.value })} />
                <button className="primary-button" type="submit">Guardar inventario</button>
              </form>
            </article>
            <article className="panel-card list-card">
              <div className="card-head"><h3>Inventario por sucursal</h3><span>{lowStock.length} bajo</span></div>
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Producto</th><th>Sucursal</th><th>Stock</th><th>Minimo</th><th>Ubicacion</th></tr></thead>
                  <tbody>{inventories.map((item) => (
                    <tr key={item.idInventario}>
                      <td>{item.nombreProducto ?? item.productoId}</td>
                      <td>{item.sucursal ?? '-'}</td>
                      <td>{item.stockActual}</td>
                      <td>{item.stockMinimo}</td>
                      <td>{item.ubicacionBodega ?? '-'}</td>
                    </tr>
                  ))}</tbody>
                </table>
              </div>
            </article>
          </section>
        )}

        {activeView === 'contador' && (
          <section className="panel-grid">
            <article className="panel-card metric-card">
              <p className="sidebar-label">Ventas registradas</p>
              <strong>{money(totalSales)}</strong>
              <p className="muted-copy">{orders.length} pedidos totales · {pendingTransfers.length} transferencias pendientes</p>
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
        )}

        {activeView === 'admin' && (
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
                    <option value="vendedor">vendedor</option>
                    <option value="bodeguero">bodeguero</option>
                    <option value="contador">contador</option>
                    <option value="admin">admin</option>
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
        )}
      </main>
    </div>
  );
}
