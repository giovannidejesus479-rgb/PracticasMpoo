import java.time.LocalDate;
import java.util.Optional;

/* =========================================================
    ENUMS
   ========================================================= */

enum CategoriaProducto {
    BEBIDA,
    SNACK
}

enum ResultadoRegistro {
    PRODUCTO_REGISTRADO,
    CODIGO_DUPLICADO,
    PRODUCTO_INVALIDO,
    PRECIO_INVALIDO
}

enum ResultadoReabastecimiento {
    REABASTECIMIENTO_REALIZADO,
    PRODUCTO_NO_ENCONTRADO,
    CANTIDAD_INVALIDA
}

enum ResultadoEntrega {
    PRODUCTO_PREPARADO,
    PRODUCTO_NO_ENCONTRADO,
    SIN_EXISTENCIAS
}

enum EstadoCobro {
    COBRO_REALIZADO,
    MONTO_INVALIDO,
    SIN_FONDOS_SUFICIENTES
}

enum EstadoCompra {
    VENTA_REALIZADA,
    PRODUCTO_NO_ENCONTRADO,
    SIN_EXISTENCIAS,
    PAGO_INSUFICIENTE,
    SIN_FONDOS_SUFICIENTES
}

/* =========================================================
    CLASES DE RESULTADO Y TRANSFERENCIA
   ========================================================= */

class ProductoInventario {
    private final Producto producto;
    private final double precio;
    private final int existencias;

    public ProductoInventario(Producto producto, double precio, int existencias) {
        this.producto = producto;
        this.precio = precio;
        this.existencias = existencias;
    }

    public Producto getProducto() {
        return producto;
    }

    public double getPrecio() {
        return precio;
    }

    public int getExistencias() {
        return existencias;
    }
}

class ResultadoCobro {
    private final EstadoCobro estado;
    private final double cambio;

    public ResultadoCobro(EstadoCobro estado, double cambio) {
        this.estado = estado;
        this.cambio = cambio;
    }

    public EstadoCobro getEstado() {
        return estado;
    }

    public double getCambio() {
        return cambio;
    }
}

class ResultadoCompra {
    private final EstadoCompra estado;
    private final Producto producto;
    private final double cambio;

    public ResultadoCompra(EstadoCompra estado, Producto producto, double cambio) {
        this.estado = estado;
        this.producto = producto;
        this.cambio = cambio;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public Producto getProducto() {
        return producto;
    }

    public double getCambio() {
        return cambio;
    }
}

/* =========================================================
    DTOS DE ENTRADA
   ========================================================= */

class RegistroProductoDTO {
    private final String codigo;
    private final String nombre;
    private final LocalDate fechaCaducidad;
    private final CategoriaProducto categoria;
    private final double precio;

    public RegistroProductoDTO(String codigo, String nombre, LocalDate fechaCaducidad, CategoriaProducto categoria, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaCaducidad = fechaCaducidad;
        this.categoria = categoria;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }
}

class ReabastecimientoDTO {
    private final String codigo;
    private final int cantidad;

    public ReabastecimientoDTO(String codigo, int cantidad) {
        this.codigo = codigo;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCantidad() {
        return cantidad;
    }
}

class CompraDTO {
    private final String codigoProducto;
    private final double cantidadRecibida;

    public CompraDTO(String codigoProducto, double cantidadRecibida) {
        this.codigoProducto = codigoProducto;
        this.cantidadRecibida = cantidadRecibida;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public double getCantidadRecibida() {
        return cantidadRecibida;
    }
}

/* =========================================================
    INTERFACES
   ========================================================= */

interface Inventario {
    ResultadoRegistro registrarProducto(Producto producto, double precio);
    Optional<ProductoInventario> buscarProducto(String codigo);
    ResultadoReabastecimiento reabastecer(String codigo, int cantidad);
    ResultadoEntrega despachar(String codigo);
}

interface Monedero {
    ResultadoCobro cobrar(double precio, double cantidadRecibida);
    double consultarFondos();
}

interface MaquinaDespachadora {
    ResultadoRegistro registrarProducto(RegistroProductoDTO producto);
    Optional<ProductoInventario> buscarProducto(String codigo);
    ResultadoReabastecimiento reabastecer(ReabastecimientoDTO solicitud);
    ResultadoCompra comprar(CompraDTO solicitud);
    double consultarFondos();
}

/* =========================================================
    IMPLEMENTACIONES
   ========================================================= */

class Producto {
    private final String codigo;
    private final String nombre;
    private final LocalDate fechaCaducidad;
    private final CategoriaProducto categoria;

    public Producto(String codigo, String nombre, LocalDate fechaCaducidad, CategoriaProducto categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaCaducidad = fechaCaducidad;
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }
}

class InventarioImpl implements Inventario {
    private final java.util.Map<String, ProductoInventario> productos = new java.util.HashMap<>();

    @Override
    public ResultadoRegistro registrarProducto(Producto producto, double precio) {
        if (producto == null || producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }
        if (precio <= 0) {
            return ResultadoRegistro.PRECIO_INVALIDO;
        }
        if (productos.containsKey(producto.getCodigo())) {
            return ResultadoRegistro.CODIGO_DUPLICADO;
        }
        
        productos.put(producto.getCodigo(), new ProductoInventario(producto, precio, 0));
        return ResultadoRegistro.PRODUCTO_REGISTRADO;
    }

    @Override
    public Optional<ProductoInventario> buscarProducto(String codigo) {
        return Optional.ofNullable(productos.get(codigo));
    }

    @Override
    public ResultadoReabastecimiento reabastecer(String codigo, int cantidad) {
        if (cantidad <= 0) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }
        ProductoInventario pi = productos.get(codigo);
        if (pi == null) {
            return ResultadoReabastecimiento.PRODUCTO_NO_ENCONTRADO;
        }

        productos.put(codigo, new ProductoInventario(pi.getProducto(), pi.getPrecio(), pi.getExistencias() + cantidad));
        return ResultadoReabastecimiento.REABASTECIMIENTO_REALIZADO;
    }

    @Override
    public ResultadoEntrega despachar(String codigo) {
        ProductoInventario pi = productos.get(codigo);
        if (pi == null) {
            return ResultadoEntrega.PRODUCTO_NO_ENCONTRADO;
        }
        if (pi.getExistencias() <= 0) {
            return ResultadoEntrega.SIN_EXISTENCIAS;
        }

        productos.put(codigo, new ProductoInventario(pi.getProducto(), pi.getPrecio(), pi.getExistencias() - 1));
        return ResultadoEntrega.PRODUCTO_PREPARADO;
    }
}

class MonederoImpl implements Monedero {
    private double fondos;

    public MonederoImpl(double fondosIniciales) {
        this.fondos = Math.max(0.0, fondosIniciales);
    }

    @Override
    public double consultarFondos() {
        return fondos;
    }

    @Override
    public ResultadoCobro cobrar(double precio, double cantidadRecibida) {
        if (precio <= 0 || cantidadRecibida <= 0) {
            return new ResultadoCobro(EstadoCobro.MONTO_INVALIDO, 0.0);
        }
        if (cantidadRecibida < precio) {
            return new ResultadoCobro(EstadoCobro.MONTO_INVALIDO, 0.0);
        }

        double cambio = cantidadRecibida - precio;
        if (cambio > fondos) {
            return new ResultadoCobro(EstadoCobro.SIN_FONDOS_SUFICIENTES, 0.0);
        }

        fondos += cantidadRecibida;
        fondos -= cambio;

        return new ResultadoCobro(EstadoCobro.COBRO_REALIZADO, cambio);
    }
}

class MaquinaExpendedora implements MaquinaDespachadora {
    private final Inventario inventario;
    private final Monedero monedero;

    private MaquinaExpendedora(Builder builder) {
        this.inventario = builder.inventario;
        this.monedero = builder.monedero;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ResultadoRegistro registrarProducto(RegistroProductoDTO dto) {
        if (dto == null) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }
        Producto producto = new Producto(dto.getCodigo(), dto.getNombre(), dto.getFechaCaducidad(), dto.getCategoria());
        return inventario.registrarProducto(producto, dto.getPrecio());
    }

    @Override
    public Optional<ProductoInventario> buscarProducto(String codigo) {
        return inventario.buscarProducto(codigo);
    }

    @Override
    public ResultadoReabastecimiento reabastecer(ReabastecimientoDTO solicitud) {
        if (solicitud == null) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }
        return inventario.reabastecer(solicitud.getCodigo(), solicitud.getCantidad());
    }

    @Override
    public ResultadoCompra comprar(CompraDTO solicitud) {
        if (solicitud == null) {
            return new ResultadoCompra(EstadoCompra.PAGO_INSUFICIENTE, null, 0.0);
        }

        Optional<ProductoInventario> optPi = inventario.buscarProducto(solicitud.getCodigoProducto());
        if (optPi.isEmpty()) {
            return new ResultadoCompra(EstadoCompra.PRODUCTO_NO_ENCONTRADO, null, 0.0);
        }

        ProductoInventario pi = optPi.get();
        if (pi.getExistencias() <= 0) {
            return new ResultadoCompra(EstadoCompra.SIN_EXISTENCIAS, pi.getProducto(), 0.0);
        }

        if (solicitud.getCantidadRecibida() < pi.getPrecio()) {
            return new ResultadoCompra(EstadoCompra.PAGO_INSUFICIENTE, pi.getProducto(), 0.0);
        }

        double cambio = solicitud.getCantidadRecibida() - pi.getPrecio();
        if (cambio > monedero.consultarFondos()) {
            return new ResultadoCompra(EstadoCompra.SIN_FONDOS_SUFICIENTES, pi.getProducto(), 0.0);
        }

        ResultadoCobro resCobro = monedero.cobrar(pi.getPrecio(), solicitud.getCantidadRecibida());
        if (resCobro.getEstado() != EstadoCobro.COBRO_REALIZADO) {
            EstadoCompra estadoMapeado = resCobro.getEstado() == EstadoCobro.SIN_FONDOS_SUFICIENTES 
                ? EstadoCompra.SIN_FONDOS_SUFICIENTES 
                : EstadoCompra.PAGO_INSUFICIENTE;
            return new ResultadoCompra(estadoMapeado, pi.getProducto(), 0.0);
        }

        ResultadoEntrega resEntrega = inventario.despachar(solicitud.getCodigoProducto());
        if (resEntrega != ResultadoEntrega.PRODUCTO_PREPARADO) {
            monedero.cobrar(cambio, pi.getPrecio());
            EstadoCompra estadoMapeado = resEntrega == ResultadoEntrega.SIN_EXISTENCIAS 
                ? EstadoCompra.SIN_EXISTENCIAS 
                : EstadoCompra.PRODUCTO_NO_ENCONTRADO;
            return new ResultadoCompra(estadoMapeado, pi.getProducto(), 0.0);
        }

        return new ResultadoCompra(EstadoCompra.VENTA_REALIZADA, pi.getProducto(), resCobro.getCambio());
    }

    @Override
    public double consultarFondos() {
        return monedero.consultarFondos();
    }

    public static class Builder {
        private Inventario inventario;
        private Monedero monedero;

        public Builder inventario(Inventario inventario) {
            this.inventario = inventario;
            return this;
        }

        public Builder monedero(Monedero monedero) {
            this.monedero = monedero;
            return this;
        }

        public MaquinaExpendedora build() {
            if (this.inventario == null) {
                this.inventario = new InventarioImpl();
            }
            if (this.monedero == null) {
                this.monedero = new MonederoImpl(0.0);
            }
            return new MaquinaExpendedora(this);
        }
    }
}

class Result {
    public static MaquinaDespachadora crearMaquina(double fondosIniciales) {
        Inventario inventario = new InventarioImpl();
        Monedero monedero = new MonederoImpl(fondosIniciales);
        return MaquinaExpendedora.builder()
                .inventario(inventario)
                .monedero(monedero)
                .build();
    }
}

/* =========================================================
    CLASE PRINCIPAL
   ========================================================= */
public class SolutionLocal {
    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.US);
        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(System.in)
        );

        System.out.println("Introduce los fondos iniciales de la maquina:");
        double fondosIniciales = Double.parseDouble(reader.readLine().trim());
        MaquinaDespachadora maquina = Result.crearMaquina(fondosIniciales);

        if (maquina == null) {
            throw new IllegalStateException("Result.crearMaquina() regreso null");
        }

        System.out.println("Introduce el numero de operaciones:");
        int numeroOperaciones = Integer.parseInt(reader.readLine().trim());

        System.out.println("Introduce las operaciones linea por linea:");
        java.util.List<String> resultados = new java.util.ArrayList<>();

        for (int i = 0; i < numeroOperaciones; i++) {
            String linea = reader.readLine();
            if (linea == null) {
                break;
            }
            String salidaOperacion = ejecutarOperacion(maquina, linea);
            if (salidaOperacion != null) {
                resultados.add(salidaOperacion);
            }
        }

        System.out.println("\n\n");
        for (String res : resultados) {
            System.out.println(res);
        }
    }

    private static String ejecutarOperacion(MaquinaDespachadora maquina, String linea) {
        String[] partes = linea.split("\\|", -1);
        String operacion = partes[0].trim().toUpperCase();

        switch (operacion) {
            case "REGISTRAR":
                return registrar(maquina, partes);
            case "BUSCAR":
                return buscar(maquina, partes);
            case "REABASTECER":
                return reabastecer(maquina, partes);
            case "COMPRAR":
                return comprar(maquina, partes);
            case "FONDOS":
                return consultarFondos(maquina);
            default:
                throw new IllegalArgumentException("Operacion desconocida: " + operacion);
        }
    }

    private static String registrar(MaquinaDespachadora maquina, String[] partes) {
        RegistroProductoDTO producto = new RegistroProductoDTO(
                partes[1].trim(),
                partes[2].trim(),
                LocalDate.parse(partes[3].trim()),
                CategoriaProducto.valueOf(partes[4].trim().toUpperCase()),
                Double.parseDouble(partes[5].trim())
        );

        ResultadoRegistro resultado = maquina.registrarProducto(producto);
        return "REGISTRAR|" + resultado;
    }

    private static String buscar(MaquinaDespachadora maquina, String[] partes) {
        String codigo = partes[1].trim();
        Optional<ProductoInventario> resultado = maquina.buscarProducto(codigo);

        if (resultado.isEmpty()) {
            return "BUSCAR|NO_ENCONTRADO";
        }

        ProductoInventario producto = resultado.get();
        return String.format(java.util.Locale.US, "BUSCAR|ENCONTRADO|%.2f|%d", producto.getPrecio(), producto.getExistencias());
    }

    private static String reabastecer(MaquinaDespachadora maquina, String[] partes) {
        ReabastecimientoDTO solicitud = new ReabastecimientoDTO(
                partes[1].trim(),
                Integer.parseInt(partes[2].trim())
        );

        ResultadoReabastecimiento resultado = maquina.reabastecer(solicitud);
        return "REABASTECER|" + resultado;
    }

    private static String comprar(MaquinaDespachadora maquina, String[] partes) {
        CompraDTO solicitud = new CompraDTO(
                partes[1].trim(),
                Double.parseDouble(partes[2].trim())
        );

        ResultadoCompra resultado = maquina.comprar(solicitud);
        return String.format(java.util.Locale.US, "COMPRAR|%s|%.2f", resultado.getEstado(), resultado.getCambio());
    }

    private static String consultarFondos(MaquinaDespachadora maquina) {
        return String.format(java.util.Locale.US, "FONDOS|%.2f", maquina.consultarFondos());
    }
}