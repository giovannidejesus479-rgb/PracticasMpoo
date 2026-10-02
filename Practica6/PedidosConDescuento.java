import java.io.*;
import java.util.*;


/* =====================================================
   ENUMERACIONES BASE
   ===================================================== */

enum AccionPedido {
    AGREGAR,
    CONFIRMAR
}


enum TipoDescuento {
    REGULAR,
    FRECUENTE,
    MAYOREO
}


enum EstadoAgregarProducto {
    AGREGADO,
    PRODUCTO_NO_EXISTE,
    CANTIDAD_INVALIDA
}


/* =====================================================
   DTO DE PRODUCTO
   ===================================================== */

final class ProductoDTO {

    private final String id;
    private final String nombre;
    private final String precio;


    public ProductoDTO(
            String id,
            String nombre,
            String precio) {

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }


    public String getId() {
        return id;
    }


    public String getNombre() {
        return nombre;
    }


    public String getPrecio() {
        return precio;
    }
}


/* =====================================================
   DTO DE DETALLE DEL PEDIDO
   ===================================================== */

final class DetallePedidoDTO {

    private final int idProducto;
    private final String nombre;
    private final int cantidad;


    public DetallePedidoDTO(
            int idProducto,
            String nombre,
            int cantidad) {

        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }


    public int getIdProducto() {
        return idProducto;
    }


    public String getNombre() {
        return nombre;
    }


    public int getCantidad() {
        return cantidad;
    }
}


/* =====================================================
   DTO DEL RESUMEN DEL PEDIDO
   ===================================================== */

final class ResumenPedidoDTO {

    private final List<DetallePedidoDTO> detalles;

    private final double subtotal;
    private final double descuento;
    private final double total;


    public ResumenPedidoDTO(
            List<DetallePedidoDTO> detalles,
            double subtotal,
            double descuento,
            double total) {

        this.detalles = detalles;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
    }


    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }


    public double getSubtotal() {
        return subtotal;
    }


    public double getDescuento() {
        return descuento;
    }


    public double getTotal() {
        return total;
    }
}


/* =====================================================
   STRATEGY
   ===================================================== */

interface DescuentoStrategy {

    double calcularDescuento(
            double subtotal);
}


/* =====================================================
   CONTRATO PARA OBJETOS CON DESCUENTO
   ===================================================== */

interface Discountable {

    void setDiscount(
            DescuentoStrategy descuentoStrategy);
}


/* =====================================================
   REPOSITORIO DE PRODUCTOS
   ===================================================== */

interface ProductoRepository {

    /*
     * Registra la informacion de un producto.
     *
     * El DTO debera transformarse al modelo
     * de dominio definido por el alumno.
     */
    boolean guardar(
            ProductoDTO producto);


    /*
     * Busca un producto utilizando su
     * identificador.
     *
     * Producto debera ser implementado
     * por el alumno.
     */
    Producto buscarPorId(
            int idProducto);


    /*
     * Indica si un producto se encuentra
     * registrado.
     */
    boolean existe(
            int idProducto);
}


/* =====================================================
   SERVICIO DE PEDIDOS
   ===================================================== */

interface PedidoService {

    /*
     * Agrega un producto al pedido.
     *
     * Debe validar que:
     *
     * - el producto exista;
     * - la cantidad sea mayor que cero.
     *
     * Una operacion invalida no debera
     * modificar el pedido.
     */
    EstadoAgregarProducto agregarProducto(
            Pedido pedido,
            int idProducto,
            int cantidad);


    /*
     * Selecciona la implementacion de
     * DescuentoStrategy correspondiente
     * al tipo solicitado.
     */
    DescuentoStrategy SelectorDescuento(
            TipoDescuento tipoDescuento);


    /*
     * Confirma el pedido y devuelve
     * su resumen.
     *
     * IMPORTANTE:
     *
     * Este metodo debera llamar internamente:
     *
     *      SelectorDescuento(...)
     *
     * y posteriormente asignar la estrategia
     * obtenida al pedido mediante:
     *
     *      pedido.setDiscount(...)
     */
    ResumenPedidoDTO confirmarPedido(
            Pedido pedido,
            TipoDescuento tipoDescuento);
}

/*
 * =====================================================
 * IMPLEMENTACION DEL ALUMNO
 * =====================================================
 *
 * Construya las clases e implementaciones necesarias
 * para resolver el problema.
 *
 *
 * =====================================================
 * 1. CAPA DE DOMINIO
 * =====================================================
 *
 * Como minimo debera crear:
 *
 *      Producto
 *
 *      ElementoPedido
 *
 *      Pedido
 *
 *
 * -----------------------------------------------------
 * PRODUCTO
 * -----------------------------------------------------
 *
 * Cada producto debera conservar:
 *
 *      - identificador
 *      - nombre
 *      - precio
 *
 *
 * -----------------------------------------------------
 * ELEMENTO PEDIDO
 * -----------------------------------------------------
 *
 * Debera representar un producto agregado al pedido
 * junto con la cantidad solicitada.
 *
 * Si un mismo producto se agrega nuevamente,
 * la cantidad debera acumularse en un unico
 * elemento del pedido.
 *
 *
 * -----------------------------------------------------
 * PEDIDO
 * -----------------------------------------------------
 *
 * Pedido debera implementar:
 *
 *      Discountable
 *
 *
 * y proporcionar obligatoriamente:
 *
 *      public static Pedido empty()
 *
 *
 * Este metodo debera crear un nuevo pedido
 * inicialmente sin productos.
 *
 *
 * Tambien debera implementar:
 *
 *      void setDiscount(
 *              DescuentoStrategy descuentoStrategy)
 *
 *
 * =====================================================
 * 2. ESTRATEGIAS DE DESCUENTO
 * =====================================================
 *
 * Cree las implementaciones necesarias de:
 *
 *      DescuentoStrategy
 *
 *
 * Deberan respetarse las siguientes politicas:
 *
 *
 * REGULAR
 *
 *      Descuento = 0 %
 *
 *
 * FRECUENTE
 *
 *      Descuento = 10 % del subtotal.
 *
 *
 * MAYOREO
 *
 *      Descuento = 15 % cuando:
 *
 *              subtotal >= 5000.00
 *
 *      En caso contrario:
 *
 *              descuento = 0
 *
 *
 * Las reglas de descuento deberan encontrarse
 * dentro de las implementaciones de
 * DescuentoStrategy.
 *
 *
 * =====================================================
 * 3. REPOSITORIO DE PRODUCTOS
 * =====================================================
 *
 * Implemente:
 *
 *      ProductoRepositoryImpl
 *
 *
 * La clase debera implementar:
 *
 *      ProductoRepository
 *
 *
 * Debera contar con un constructor publico
 * sin parametros:
 *
 *      public ProductoRepositoryImpl()
 *
 *
 * El repositorio sera responsable de almacenar
 * los productos disponibles.
 *
 * Seleccione la estructura de datos que considere
 * adecuada.
 *
 *
 * =====================================================
 * 4. SERVICIO DE PEDIDOS
 * =====================================================
 *
 * Implemente:
 *
 *      PedidoServiceImpl
 *
 *
 * La clase debera implementar:
 *
 *      PedidoService
 *
 *
 * PedidoServiceImpl debera recibir mediante
 * constructor una instancia de:
 *
 *      ProductoRepository
 *
 *
 * Por lo tanto, debera existir el constructor:
 *
 *      public PedidoServiceImpl(
 *              ProductoRepository productoRepository)
 *
 *
 * =====================================================
 * 5. AGREGAR PRODUCTO
 * =====================================================
 *
 * El metodo:
 *
 *      agregarProducto(
 *              Pedido pedido,
 *              int idProducto,
 *              int cantidad)
 *
 *
 * debera realizar las validaciones necesarias.
 *
 *
 * Si el producto no existe:
 *
 *      PRODUCTO_NO_EXISTE
 *
 *
 * Si la cantidad es menor o igual que cero:
 *
 *      CANTIDAD_INVALIDA
 *
 *
 * Si la operacion es valida:
 *
 *      AGREGADO
 *
 *
 * Una operacion invalida no debera modificar
 * el estado anterior del pedido.
 *
 *
 * Si un producto ya se encuentra dentro del pedido,
 * la nueva cantidad debera acumularse con la
 * existente.
 *
 *
 * =====================================================
 * 6. SELECTOR DE DESCUENTO
 * =====================================================
 *
 * El metodo:
 *
 *      SelectorDescuento(
 *              TipoDescuento tipoDescuento)
 *
 *
 * debera regresar una instancia de
 * DescuentoStrategy de acuerdo con el tipo
 * solicitado.
 *
 *
 * Puede utilizar:
 *
 *      switch
 *
 *      if / else
 *
 * u otra solucion equivalente.
 *
 *
 * =====================================================
 * 7. CONFIRMAR PEDIDO
 * =====================================================
 *
 * El metodo:
 *
 *      confirmarPedido(
 *              Pedido pedido,
 *              TipoDescuento tipoDescuento)
 *
 *
 * debera realizar obligatoriamente el siguiente
 * flujo:
 *
 *
 *      TipoDescuento
 *              |
 *              v
 *      SelectorDescuento(...)
 *              |
 *              v
 *      DescuentoStrategy
 *              |
 *              v
 *      pedido.setDiscount(...)
 *              |
 *              v
 *      calcular subtotal
 *              |
 *              v
 *      calcular descuento
 *              |
 *              v
 *      calcular total
 *              |
 *              v
 *      ResumenPedidoDTO
 *
 *
 * IMPORTANTE:
 *
 * confirmarPedido() debera invocar
 * SelectorDescuento().
 *
 * No debera duplicarse dentro de confirmarPedido()
 * la logica utilizada para decidir que estrategia
 * corresponde a cada TipoDescuento.
 *
 *
 * =====================================================
 * 8. RESUMEN DEL PEDIDO
 * =====================================================
 *
 * confirmarPedido() debera regresar:
 *
 *      ResumenPedidoDTO
 *
 *
 * El resumen debera contener:
 *
 *      List<DetallePedidoDTO>
 *
 *      subtotal
 *
 *      descuento
 *
 *      total
 *
 *
 * Cada DetallePedidoDTO debera contener:
 *
 *      idProducto
 *
 *      nombre
 *
 *      cantidad
 *
 *
 * Los productos deberan conservar en el resumen
 * el orden en el que fueron agregados por primera
 * vez al pedido.
 *
 *
 * Si el pedido no contiene productos validos,
 * debera regresarse un resumen con:
 *
 *      detalles = lista vacia
 *
 *      subtotal = 0.00
 *
 *      descuento = 0.00
 *
 *      total = 0.00
 *
 *
 * =====================================================
 * DESARROLLE SU SOLUCION A PARTIR DE AQUI
 * =====================================================
 */
 
 /*
 * =====================================================
 * IMPLEMENTACION DEL ALUMNO
 * =====================================================
 */

// =====================================================
// 1. CAPA DE DOMINIO
// =====================================================

class Producto {
    private final int id;
    private final String nombre;
    private final double precio;

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
}

class ElementoPedido {
    private final Producto producto;
    private int cantidad;

    public ElementoPedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    
    public void acumularCantidad(int extra) { 
        this.cantidad += extra; 
    }
    
    public double getSubtotal() { 
        return producto.getPrecio() * cantidad; 
    }
}

class Pedido implements Discountable {
    // LinkedHashMap mantiene el orden en el que se agregaron los elementos
    private final Map<Integer, ElementoPedido> elementos;
    private DescuentoStrategy descuentoStrategy;

    private Pedido() {
        this.elementos = new LinkedHashMap<>();
    }

    public static Pedido empty() {
        return new Pedido();
    }

    public void setDiscount(DescuentoStrategy descuentoStrategy) {
        this.descuentoStrategy = descuentoStrategy;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        int id = producto.getId();
        if (elementos.containsKey(id)) {
            elementos.get(id).acumularCantidad(cantidad);
        } else {
            elementos.put(id, new ElementoPedido(producto, cantidad));
        }
    }

    public List<ElementoPedido> getElementos() {
        return new ArrayList<>(elementos.values());
    }
}

// =====================================================
// 2. ESTRATEGIAS DE DESCUENTO
// =====================================================

class EstrategiaRegular implements DescuentoStrategy {
    
    public double calcularDescuento(double subtotal) {
        return 0.0;
    }
}

class EstrategiaFrecuente implements DescuentoStrategy {
    
    public double calcularDescuento(double subtotal) {
        return subtotal * 0.10; // 10%
    }
}

class EstrategiaMayoreo implements DescuentoStrategy {
    
    public double calcularDescuento(double subtotal) {
        if (subtotal >= 5000.00) {
            return subtotal * 0.15; // 15%
        }
        return 0.0;
    }
}

// =====================================================
// 3. REPOSITORIO DE PRODUCTOS
// =====================================================

class ProductoRepositoryImpl implements ProductoRepository {
    private final Map<Integer, Producto> catalogo;

    public ProductoRepositoryImpl() {
        this.catalogo = new HashMap<>();
    }

    
    public boolean guardar(ProductoDTO productoDTO) {
        try {
            int id = Integer.parseInt(productoDTO.getId());
            double precio = Double.parseDouble(productoDTO.getPrecio());
            Producto producto = new Producto(id, productoDTO.getNombre(), precio);
            catalogo.put(id, producto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
    public Producto buscarPorId(int idProducto) {
        return catalogo.get(idProducto);
    }

    
    public boolean existe(int idProducto) {
        return catalogo.containsKey(idProducto);
    }
}

// =====================================================
// 4. SERVICIO DE PEDIDOS
// =====================================================

class PedidoServiceImpl implements PedidoService {
    private final ProductoRepository productoRepository;

    public PedidoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public EstadoAgregarProducto agregarProducto(Pedido pedido, int idProducto, int cantidad) {
        if (!productoRepository.existe(idProducto)) {
            return EstadoAgregarProducto.PRODUCTO_NO_EXISTE;
        }
        if (cantidad <= 0) {
            return EstadoAgregarProducto.CANTIDAD_INVALIDA;
        }
        
        Producto producto = productoRepository.buscarPorId(idProducto);
        pedido.agregarProducto(producto, cantidad);
        
        return EstadoAgregarProducto.AGREGADO;
    }

    public DescuentoStrategy SelectorDescuento(TipoDescuento tipoDescuento) {
        switch (tipoDescuento) {
            case FRECUENTE:
                return new EstrategiaFrecuente();
            case MAYOREO:
                return new EstrategiaMayoreo();
            case REGULAR:
            default:
                return new EstrategiaRegular();
        }
    }

    public ResumenPedidoDTO confirmarPedido(Pedido pedido, TipoDescuento tipoDescuento) {
        // 1. Obtener y asignar la estrategia
        DescuentoStrategy estrategia = SelectorDescuento(tipoDescuento);
        pedido.setDiscount(estrategia);

        // 2. Calcular subtotal y mapear los detalles
        double subtotal = 0.0;
        List<DetallePedidoDTO> detalles = new ArrayList<>();

        for (ElementoPedido elemento : pedido.getElementos()) {
            Producto producto = elemento.getProducto();
            subtotal += elemento.getSubtotal();
            
            detalles.add(new DetallePedidoDTO(
                producto.getId(), 
                producto.getNombre(), 
                elemento.getCantidad()
            ));
        }

        // 3. Calcular descuentos y totales de acuerdo la estrategia
        double descuento = estrategia.calcularDescuento(subtotal);
        double total = subtotal - descuento;

        // 4. Retornar el DTO con la informacion final
        return new ResumenPedidoDTO(detalles, subtotal, descuento, total);
    }
}

public class PedidosConDescuento {

    public static void main(String[] args)
            throws Exception {


        /*
         * Los importes deberan imprimirse
         * utilizando punto decimal.
         */
        Locale.setDefault(Locale.US);


        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));


        /*
         * =================================================
         * LECTURA DE LA ENTRADA
         * =================================================
         */

        String productos =
                br.readLine();


        String operaciones =
                br.readLine();


        /*
         * =================================================
         * CREACION DE DEPENDENCIAS
         * =================================================
         *
         * El main unicamente trabaja con las interfaces.
         */

        ProductoRepository productoRepository =
                new ProductoRepositoryImpl();


        PedidoService pedidoService =
                new PedidoServiceImpl(
                        productoRepository);


        Pedido pedido =
                Pedido.empty();


        /*
         * =================================================
         * REGISTRO DEL CATALOGO DE PRODUCTOS
         * =================================================
         */

        if (productos != null
                && !productos.isBlank()
                && !productos.equals("-")) {


            String[] registros =
                    productos.split(";");


            for (String registro : registros) {


                /*
                 * Formato:
                 *
                 * id|nombre|precio
                 */

                String[] datos =
                        registro.split("\\|", 3);


                ProductoDTO productoDTO =
                        new ProductoDTO(
                                datos[0].trim(),
                                datos[1].trim(),
                                datos[2].trim()
                        );


                /*
                 * El main no almacena productos.
                 *
                 * Esa responsabilidad pertenece
                 * al repositorio.
                 */
                productoRepository.guardar(
                        productoDTO);
            }
        }


        /*
         * =================================================
         * PROCESAMIENTO DE OPERACIONES
         * =================================================
         */

        StringBuilder salida =
                new StringBuilder();


        if (operaciones != null
                && !operaciones.isBlank()
                && !operaciones.equals("-")) {


            String[] listaOperaciones =
                    operaciones.split(";");


            for (String operacion
                    : listaOperaciones) {


                String[] datos =
                        operacion.split("\\|");


                AccionPedido accion =
                        AccionPedido.valueOf(
                                datos[0].trim());


                /*
                 * =========================================
                 * AGREGAR PRODUCTO
                 * =========================================
                 */

                if (accion
                        == AccionPedido.AGREGAR) {


                    int idProducto =
                            Integer.parseInt(
                                    datos[1].trim());


                    int cantidad =
                            Integer.parseInt(
                                    datos[2].trim());


                    /*
                     * Toda validacion corresponde
                     * al servicio.
                     */
                    EstadoAgregarProducto estado =
                            pedidoService
                                    .agregarProducto(
                                            pedido,
                                            idProducto,
                                            cantidad
                                    );


                    salida.append("AGREGAR ")
                            .append(idProducto)
                            .append(" ")
                            .append(estado)
                            .append("\n");
                }


                /*
                 * =========================================
                 * CONFIRMAR PEDIDO
                 * =========================================
                 */

                else if (accion
                        == AccionPedido.CONFIRMAR) {


                    TipoDescuento tipoDescuento =
                            TipoDescuento.valueOf(
                                    datos[1].trim());


                    /*
                     * El main NO llama directamente a:
                     *
                     *      SelectorDescuento(...)
                     *
                     * ni a:
                     *
                     *      pedido.setDiscount(...)
                     *
                     *
                     * confirmarPedido(...) debera
                     * encargarse de ese flujo.
                     */
                    ResumenPedidoDTO resumen =
                            pedidoService
                                    .confirmarPedido(
                                            pedido,
                                            tipoDescuento
                                    );


                    if (resumen == null) {

                        throw new IllegalStateException(
                                "confirmarPedido no debe "
                                + "regresar null"
                        );
                    }


                    imprimirResumen(
                            salida,
                            resumen);
                }
            }
        }


        /*
         * =================================================
         * SALIDA
         * =================================================
         */

        System.out.print(
                salida.toString());
    }


    /*
     * =====================================================
     * IMPRESION DEL RESUMEN
     * =====================================================
     *
     * Este metodo unicamente transforma el DTO
     * resultante al formato esperado por HackerRank.
     *
     * No realiza calculos de negocio.
     */

    private static void imprimirResumen(
            StringBuilder salida,
            ResumenPedidoDTO resumen) {


        List<DetallePedidoDTO> detalles =
                resumen.getDetalles();


        if (detalles == null) {

            throw new IllegalStateException(
                    "La lista de detalles "
                    + "no debe ser null"
            );
        }


        /*
         * -------------------------------------------------
         * DETALLE DE PRODUCTOS
         * -------------------------------------------------
         */

        for (DetallePedidoDTO detalle
                : detalles) {


            salida.append("ITEM ")
                    .append(
                            detalle.getIdProducto())
                    .append(" ")
                    .append(
                            detalle.getNombre())
                    .append(" ")
                    .append(
                            detalle.getCantidad())
                    .append("\n");
        }


        /*
         * -------------------------------------------------
         * RESUMEN ECONOMICO
         * -------------------------------------------------
         */

        salida.append(
                String.format(
                        Locale.US,
                        "RESUMEN %.2f %.2f %.2f",
                        resumen.getSubtotal(),
                        resumen.getDescuento(),
                        resumen.getTotal()
                )
        );
    }
}
