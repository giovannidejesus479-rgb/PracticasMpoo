import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

interface ServicioEnvio {

    double calcularCosto();

    String getDescripcion();
}

enum TipoEnvio {

    TERRESTRE,
    AEREO
}

enum ServicioAdicional {

    SEGURO,
    EXPRESS
}

class EntradaEnvioDTO {

    private final TipoEnvio tipo;
    private final String destinatario;
    private final double pesoKg;
    private final double distanciaKm;
    private final List<ServicioAdicional> servicios;


    public EntradaEnvioDTO(
            TipoEnvio tipo,
            String destinatario,
            double pesoKg,
            double distanciaKm,
            List<ServicioAdicional> servicios
    ) {

        this.tipo = tipo;
        this.destinatario = destinatario;
        this.pesoKg = pesoKg;
        this.distanciaKm = distanciaKm;

        this.servicios =
                Collections.unmodifiableList(
                        new ArrayList<>(servicios)
                );
    }


    public TipoEnvio getTipo() {
        return tipo;
    }


    public String getDestinatario() {
        return destinatario;
    }


    public double getPesoKg() {
        return pesoKg;
    }


    public double getDistanciaKm() {
        return distanciaKm;
    }


    public List<ServicioAdicional> getServicios() {
        return servicios;
    }
}

/*
 * =========================================================
 * PRACTICA 10 - LOGIPACK
 * POLIMORFISMO Y PATRON DECORATOR
 * =========================================================
 *
*/


/* =========================================================
    DESARROLLA TU SOLUCION AQUI
   ========================================================= */
abstract class Envio implements ServicioEnvio{
    protected double peso;
    protected double distancia;
    
    public Envio(double peso, double distancia){
        this.peso = peso;
        this.distancia = distancia;
    }
}

class EnvioTerrestre extends Envio {
    public EnvioTerrestre(double peso, double distancia){
        super(peso, distancia);
    }
    
    @Override
    public double calcularCosto(){
        return (distancia * 0.50) + (peso * 2);
    }
    
    @Override
    public String getDescripcion(){
        return "Envio terrestre";
    }
}

class EnvioAereo extends Envio{
    public EnvioAereo(double peso, double distancia){
        super(peso, distancia);
    }
    
    @Override
    public double calcularCosto(){
        return (distancia * 1.20) + (peso * 5);
    }
    
    @Override
    public String getDescripcion(){
        return "Envio aereo";
    }
}

abstract class ServicioAdicionalDecorator implements ServicioEnvio{
    protected ServicioEnvio envioDecorator;
    
    public ServicioAdicionalDecorator(ServicioEnvio envioDecorator){
        this.envioDecorator = envioDecorator;
    }
    
    @Override
    public double calcularCosto(){
        return envioDecorator.calcularCosto();
    }
    
    @Override
    public String getDescripcion(){
        return envioDecorator.getDescripcion();
    }
}

class SeguroEnvio extends ServicioAdicionalDecorator{
    public SeguroEnvio(ServicioEnvio envioDecorator){
        super(envioDecorator);
    }
    
    @Override
    public double calcularCosto(){
        return super.calcularCosto() * 1.10;
    }
    
    @Override
    public String getDescripcion(){
        return super.getDescripcion() + " + Seguro";
    }
}

class EntregaExpress extends ServicioAdicionalDecorator{
    public EntregaExpress(ServicioEnvio envioDecorator){
        super(envioDecorator);
    }
    
    @Override
    public double calcularCosto(){
        return super.calcularCosto() + 100;
    }
    
    @Override
    public String getDescripcion(){
        return super.getDescripcion() + " + Express";
    }
}

interface CreadorEnvio{
    ServicioEnvio crear(double peso, double distancia);
}

interface AplicadorDecorator{
    ServicioEnvio aplicar(ServicioEnvio envio);
}

class CentroEnvios{
    
    public String procesar(ServicioEnvio envio){
        return envio.getDescripcion() + "|" + String.format(Locale.US, "%.2f", envio.calcularCosto());
    }
    
    public List<String> procesar(List<ServicioEnvio> envios){
        List<String> resultados = new ArrayList<>();
        for(ServicioEnvio envio : envios){
            resultados.add(procesar(envio));
        }
        return resultados;
    }
}

class Result {

    /*
     * HackerRank enviara una lista de objetos EntradaEnvioDTO.
     *
     * Cada objeto contiene:
     *
     * - tipo de envio
     * - destinatario
     * - peso
     * - distancia
     * - servicios adicionales
     *
     * Tu objetivo es:
     *
     * 1. Crear el tipo de envio correspondiente.
     * 2. Aplicar los decoradores solicitados.
     * 3. Almacenar los envios como ServicioEnvio.
     * 4. Utilizar CentroEnvios para procesarlos.
     * 5. Regresar la lista de resultados.
     */

    public static List<String> procesar(
            List<EntradaEnvioDTO> entradas
    ) {

        List<ServicioEnvio> envios =
                new ArrayList<>();
                
                CreadorEnvio[] creadoresEnvio = new CreadorEnvio[]{
                    new CreadorEnvio() {
                        public ServicioEnvio crear(double p, double d){
                            return new EnvioTerrestre(p, d);
                        }
                    },
                    new CreadorEnvio() {
                        public ServicioEnvio crear(double p, double d){
                            return new EnvioAereo(p, d);
                        }
                    }
                };

AplicadorDecorator[] aplicadoresDecorator =new AplicadorDecorator[]{
    new AplicadorDecorator() {
        public ServicioEnvio aplicar(ServicioEnvio e){
            return new SeguroEnvio(e);
        }
    },
    new AplicadorDecorator() {
        public ServicioEnvio aplicar(ServicioEnvio e){
            return new EntregaExpress(e);
        }
    }
};

        for (EntradaEnvioDTO entrada : entradas) {
            ServicioEnvio envio = creadoresEnvio[entrada.getTipo().ordinal()].crear(entrada.getPesoKg(), entrada.getDistanciaKm());
            
            for (ServicioAdicional servicioAdicional : entrada.getServicios()){
                envio = aplicadoresDecorator[servicioAdicional.ordinal()].aplicar(envio);
            }
            
            /*
             * PASO 1
             *
             * Crear un objeto EnvioTerrestre o EnvioAereo
             * dependiendo de entrada.getTipo().
             *
             * Guarda el objeto utilizando una referencia:
             *
             * ServicioEnvio envio
             */

            //ServicioEnvio envio = null;


            /*
             * PASO 2
             *
             * Recorre:
             *
             * entrada.getServicios()
             *
             * y aplica los decoradores correspondientes.
             *
             * Ejemplo conceptual:
             *
             * envio = new SeguroEnvio(envio);
             *
             * o:
             *
             * envio = new EntregaExpress(envio);
             */


            /*
             * PASO 3
             *
             * Agrega el envio terminado a la coleccion.
             */

            envios.add(envio);
        }


        /*
         * PASO 4
         *
         * Crea un CentroEnvios.
         *
         * Utiliza su metodo procesar para procesar
         * toda la coleccion.
         */


        /*
         * PASO 5
         *
         * Regresa la lista generada por CentroEnvios.
         */
        
        CentroEnvios centroEnvios = new CentroEnvios();

        //return new ArrayList<>();
        
        return centroEnvios.procesar(envios);
    }
}

public class Solution{

    public static void main(String[] args)
            throws Exception {


        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                System.in
                        )
                );

        String primeraLinea =
                reader.readLine();


        if (
                primeraLinea == null ||
                primeraLinea.isBlank()
        ) {
            return;
        }


        int numeroEnvios =
                Integer.parseInt(
                        primeraLinea.trim()
                );


        List<EntradaEnvioDTO> entradas =
                new ArrayList<>();


        for (
                int i = 0;
                i < numeroEnvios;
                i++
        ) {

            String linea =
                    reader.readLine();


            if (linea == null) {

                throw new IllegalArgumentException(
                        "Faltan datos de entrada"
                );
            }


            EntradaEnvioDTO entrada =
                    convertirEntrada(
                            linea
                    );


            entradas.add(
                    entrada
            );
        }

        List<String> resultados =
                Result.procesar(
                        entradas
                );


        if (resultados == null) {

            throw new IllegalStateException(
                    "Result.procesar() regreso null"
            );
        }

        for (String resultado : resultados) {

            System.out.println(
                    resultado
            );
        }
    }

    private static EntradaEnvioDTO convertirEntrada(
            String linea
    ) {

        if (
                linea == null ||
                linea.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "La linea de entrada no puede estar vacia"
            );
        }


        String[] partes =
                linea.split(
                        "\\|",
                        -1
                );


        if (partes.length != 5) {

            throw new IllegalArgumentException(
                    "Formato de entrada invalido: "
                            + linea
            );
        }

        TipoEnvio tipo =
                TipoEnvio.valueOf(
                        partes[0]
                                .trim()
                                .toUpperCase(
                                        Locale.ROOT
                                )
                );


        String destinatario =
                partes[1].trim();


        if (destinatario.isBlank()) {

            throw new IllegalArgumentException(
                    "El destinatario no puede estar vacio"
            );
        }

        double pesoKg =
                Double.parseDouble(
                        partes[2].trim()
                );


        double distanciaKm =
                Double.parseDouble(
                        partes[3].trim()
                );

        List<ServicioAdicional> servicios =
                convertirServicios(
                        partes[4]
                );


        return new EntradaEnvioDTO(
                tipo,
                destinatario,
                pesoKg,
                distanciaKm,
                servicios
        );
    }

    private static List<ServicioAdicional> convertirServicios(
            String valor
    ) {

        List<ServicioAdicional> servicios =
                new ArrayList<>();


        if (valor == null) {
            return servicios;
        }


        String texto =
                valor.trim();


        if (
                texto.isEmpty() ||
                texto.equalsIgnoreCase(
                        "NINGUNO"
                )
        ) {

            return servicios;
        }


        String[] elementos =
                texto.split(
                        "\\+"
                );


        for (String elemento : elementos) {

            String nombreServicio =
                    elemento
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            );


            ServicioAdicional servicio =
                    ServicioAdicional.valueOf(
                            nombreServicio
                    );


            servicios.add(
                    servicio
            );
        }


        return servicios;
    }
}
