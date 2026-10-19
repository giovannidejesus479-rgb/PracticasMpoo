import java.io.*;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;


/*
 * =====================================================
 * TIPOS PROPORCIONADOS POR HACKERRANK
 * =====================================================
 */

enum TipoReembolso {
    ALIMENTOS,
    TRANSPORTE,
    HOSPEDAJE
}


enum ResultadoValidacion {
    APROBADO,
    SIN_COMPROBANTE,
    FUERA_DE_PERIODO,
    EXCEDE_LIMITE,
    REQUIERE_AUTORIZACION
}


/*
 * =====================================================
 * DTO DE ENTRADA
 * =====================================================
 *
 * Contiene toda la informacion necesaria para construir
 * y evaluar una solicitud de reembolso.
 */

final class ReembolsoInputDTO {

    private final TipoReembolso tipo;
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;
    private final LocalDate fechaEvaluacion;


    public ReembolsoInputDTO(
            TipoReembolso tipo,
            String folio,
            String empleado,
            double monto,
            LocalDate fechaGasto,
            boolean tieneComprobante,
            boolean autorizadoGerente,
            LocalDate fechaEvaluacion) {

        this.tipo = tipo;
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
        this.fechaEvaluacion = fechaEvaluacion;
    }


    public TipoReembolso getTipo() {
        return tipo;
    }

    public String getFolio() {
        return folio;
    }

    public String getEmpleado() {
        return empleado;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public boolean tieneComprobante() {
        return tieneComprobante;
    }

    public boolean estaAutorizadoGerente() {
        return autorizadoGerente;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }
}


/*
 * =====================================================
 * DTO DE SALIDA
 * =====================================================
 */

final class ReembolsoOutputDTO {

    private final String folio;
    private final TipoReembolso tipo;
    private final ResultadoValidacion resultado;


    public ReembolsoOutputDTO(
            String folio,
            TipoReembolso tipo,
            ResultadoValidacion resultado) {

        this.folio = folio;
        this.tipo = tipo;
        this.resultado = resultado;
    }


    public String getFolio() {
        return folio;
    }

    public TipoReembolso getTipo() {
        return tipo;
    }

    public ResultadoValidacion getResultado() {
        return resultado;
    }
}

abstract class Reembolso {
    protected TipoReembolso tipo;
    protected String folio;
    protected String empleado;
    protected double monto;
    protected LocalDate fechaGasto;
    protected boolean tieneComprobante;
    protected boolean autorizadoGerente;

    public Reembolso(TipoReembolso tipo, String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        this.tipo = tipo;
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
    }

    public Reembolso(TipoReembolso tipo, String folio, String empleado, double monto, LocalDate fechaGasto) {
        this(tipo, folio, empleado, monto, fechaGasto, false, false);
    }

    public abstract double getLimitePermitido();

    public TipoReembolso getTipo() { return tipo; }
    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
}

class ReembolsoAlimentos extends Reembolso {
    public ReembolsoAlimentos(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(TipoReembolso.ALIMENTOS, folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
    public ReembolsoAlimentos(String folio, String empleado, double monto, LocalDate fechaGasto) {
        super(TipoReembolso.ALIMENTOS, folio, empleado, monto, fechaGasto);
    }
    @Override
    public double getLimitePermitido() {
        return 1000.0;
    }
}

class ReembolsoTransporte extends Reembolso {
    public ReembolsoTransporte(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(TipoReembolso.TRANSPORTE, folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
    public ReembolsoTransporte(String folio, String empleado, double monto, LocalDate fechaGasto) {
        super(TipoReembolso.TRANSPORTE, folio, empleado, monto, fechaGasto);
    }
    @Override
    public double getLimitePermitido() {
        return 1500.0;
    }
}

class ReembolsoHospedaje extends Reembolso {
    public ReembolsoHospedaje(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(TipoReembolso.HOSPEDAJE, folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
    public ReembolsoHospedaje(String folio, String empleado, double monto, LocalDate fechaGasto) {
        super(TipoReembolso.HOSPEDAJE, folio, empleado, monto, fechaGasto);
    }
    @Override
    public double getLimitePermitido() {
        return 4000.0;
    }
}

abstract class RegistroReembolso {
    public abstract Reembolso crearReembolso(String folio, String empleado, double monto,LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente);
}

class RegistroAlimentos extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        return new ReembolsoAlimentos(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
}

class RegistroTransporte extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(String folio, String empleado, double monto, 
    LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        return new ReembolsoTransporte(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
}

class RegistroHospedaje extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        return new ReembolsoHospedaje(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }
}

abstract class ValidadorReembolso {
    protected ValidadorReembolso siguiente;

    public ValidadorReembolso setSiguiente(ValidadorReembolso siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public abstract ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion);
}

class ValidadorComprobante extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (!reembolso.tieneComprobante()) {
            return ResultadoValidacion.SIN_COMPROBANTE;
        }
        if (siguiente != null) {
            return siguiente.validar(reembolso, fechaEvaluacion);
        }
        return ResultadoValidacion.APROBADO;
    }
}

class ValidadorPeriodo extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        long diasTranscurridos = ChronoUnit.DAYS.between(reembolso.getFechaGasto(), fechaEvaluacion);
        if (diasTranscurridos > 30) {
            return ResultadoValidacion.FUERA_DE_PERIODO;
        }
        if (siguiente != null) {
            return siguiente.validar(reembolso, fechaEvaluacion);
        }
        return ResultadoValidacion.APROBADO;
    }
}

class ValidadorLimite extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > reembolso.getLimitePermitido()) {
            return ResultadoValidacion.EXCEDE_LIMITE;
        }
        if (siguiente != null) {
            return siguiente.validar(reembolso, fechaEvaluacion);
        }
        return ResultadoValidacion.APROBADO;
    }
}

class ValidadorGerencia extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > 2000 && !reembolso.estaAutorizadoGerente()) {
            return ResultadoValidacion.REQUIERE_AUTORIZACION;
        }
        if (siguiente != null) {
            return siguiente.validar(reembolso, fechaEvaluacion);
        }
        return ResultadoValidacion.APROBADO;
    }
}

class Result {

    public static ReembolsoOutputDTO procesarReembolso(
            ReembolsoInputDTO input) {

        // Desarrolle su solucion aqui.
        
        RegistroReembolso creador = null;
        switch (input.getTipo()){
            case ALIMENTOS:
            creador = new RegistroAlimentos();
            break;
            case TRANSPORTE:
            creador = new RegistroTransporte();
            break;
            case HOSPEDAJE:
            creador = new RegistroHospedaje();
            break; 
        }
        
        Reembolso reembolso = creador.crearReembolso(input.getFolio(), input.getEmpleado(), input.getMonto(), input.getFechaGasto(), input.tieneComprobante(), input.estaAutorizadoGerente());
        
        ValidadorReembolso v1 = new ValidadorComprobante();
        ValidadorReembolso v2 = new ValidadorPeriodo();
        ValidadorReembolso v3 = new ValidadorLimite();
        ValidadorReembolso v4 = new ValidadorGerencia();
        
        v1.setSiguiente(v2).setSiguiente(v3).setSiguiente(v4);
        
        ResultadoValidacion resultado = v1.validar(reembolso, input.getFechaEvaluacion());
        
        return new ReembolsoOutputDTO(reembolso.getFolio(), reembolso.getTipo(), resultado);
    }
}

public class ReembolsoGeneral {

    public static void main(String[] args)
            throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));


        /*
         * ==============================================
         * FECHA DE EVALUACION
         * ==============================================
         */

        String lineaFecha =
                br.readLine();

        LocalDate fechaEvaluacion =
                LocalDate.parse(
                        lineaFecha.trim()
                );


        /*
         * ==============================================
         * DATOS DEL REEMBOLSO
         * ==============================================
         */

        String lineaSolicitud =
                br.readLine();

        String[] datos =
                lineaSolicitud.split("\\|");


        TipoReembolso tipo =
                TipoReembolso.valueOf(
                        datos[0].trim()
                );

        String folio =
                datos[1].trim();

        String empleado =
                datos[2].trim();

        double monto =
                Double.parseDouble(
                        datos[3].trim()
                );

        LocalDate fechaGasto =
                LocalDate.parse(
                        datos[4].trim()
                );

        boolean tieneComprobante =
                Boolean.parseBoolean(
                        datos[5].trim()
                );

        boolean autorizadoGerente =
                Boolean.parseBoolean(
                        datos[6].trim()
                );


        /*
         * ==============================================
         * CREACION DEL DTO DE ENTRADA
         * ==============================================
         */

        ReembolsoInputDTO input =
                new ReembolsoInputDTO(
                        tipo,
                        folio,
                        empleado,
                        monto,
                        fechaGasto,
                        tieneComprobante,
                        autorizadoGerente,
                        fechaEvaluacion
                );


        /*
         * ==============================================
         * EJECUCION DEL CODIGO DEL ALUMNO
         * ==============================================
         */

        ReembolsoOutputDTO output =
                Result.procesarReembolso(input);


        /*
         * ==============================================
         * SALIDA
         * ==============================================
         */

        System.out.println(
                "REEMBOLSO "
                + output.getFolio()
        );

        System.out.println(
                "TIPO "
                + output.getTipo()
        );

        System.out.println(
                "RESULTADO "
                + output.getResultado()
        );
    }
}
