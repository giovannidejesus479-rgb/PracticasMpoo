import java.io.*;
import java.util.*;


/* =====================================================
   ENUMERACIONES BASE
   ===================================================== */

enum TipoAsistente {
    ALUMNO,
    PROFESOR,
    INVITADO
}

enum AccionEvento {
    REGISTRO,
    ENTRADA,
    SALIDA,
    ENTRADA_MASIVA
}

enum EstadoEntrada {
    AUTORIZADO,
    NO_REGISTRADO,
    YA_DENTRO,
    AFORO_COMPLETO
}

enum EstadoSalida {
    AUTORIZADA,
    NO_REGISTRADO,
    NO_ESTA_DENTRO
}


/* =====================================================
   DTO DE ENTRADA
   ===================================================== */

final class AsistenteDTO {

    private final String id;
    private final String nombre;
    private final String tipo;

    public AsistenteDTO(
            String id,
            String nombre,
            String tipo) {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }
}


/* =====================================================
   DTO DE RESULTADO DE ENTRADA
   ===================================================== */

final class ResultadoEntradaDTO {

    private final int idAsistente;
    private final EstadoEntrada estado;

    public ResultadoEntradaDTO(
            int idAsistente,
            EstadoEntrada estado) {

        this.idAsistente = idAsistente;
        this.estado = estado;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public EstadoEntrada getEstado() {
        return estado;
    }
}


/* =====================================================
   DTO DEL REPORTE
   ===================================================== */

final class ReporteDTO {

    private final int registrados;
    private final int disponibles;
    private final int aforo;
    private final int alumnos;
    private final int profesores;
    private final int invitados;
    private final double ocupacion;

    public ReporteDTO(
            int registrados,
            int disponibles,
            int aforo,
            int alumnos,
            int profesores,
            int invitados,
            double ocupacion) {

        this.registrados = registrados;
        this.disponibles = disponibles;
        this.aforo = aforo;
        this.alumnos = alumnos;
        this.profesores = profesores;
        this.invitados = invitados;
        this.ocupacion = ocupacion;
    }

    public int getRegistrados() {
        return registrados;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public int getAforo() {
        return aforo;
    }

    public int getAlumnos() {
        return alumnos;
    }

    public int getProfesores() {
        return profesores;
    }

    public int getInvitados() {
        return invitados;
    }

    public double getOcupacion() {
        return ocupacion;
    }
}


/* =====================================================
   CONTRATO DE LA CAPA DE APLICACION
   ===================================================== */

interface ControlAccesoService {

    int AFORO_MAXIMO = 10;

    boolean registrarAsistente(
            AsistenteDTO asistente);

    EstadoEntrada registrarEntrada(
            int idAsistente);

    EstadoSalida registrarSalida(
            int idAsistente);

    List<ResultadoEntradaDTO>
            registrarEntradasMasivas(
                    List<Integer> identificadores);

    ReporteDTO generarReporte();
}

/*
 * =====================================================
 * IMPLEMENTACION DEL ALUMNO
 * =====================================================
 *
 * Construye aqui:
 *
 * - tu capa de dominio;
 * - las clases que consideres necesarias;
 * - ControlAccesoServiceImpl.
 *
 * ControlAccesoServiceImpl debera implementar:
 *
 *      ControlAccesoService
 *
 * Los asistentes registrados deberan mantenerse
 * en una variable estatica dentro del servicio.
 *
 * No se especifica que estructuras de datos debes
 * utilizar. Selecciona las que consideres adecuadas
 * y justifica tu decision en el reporte.
 */
 
class Asistente {
    private final int id;
    private final String nombre;
    private final TipoAsistente tipo;

    public Asistente(int id, String nombre, TipoAsistente tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoAsistente getTipo() {
        return tipo;
    }
}

class ControlAccesoServiceImpl implements ControlAccesoService {
    
    private static Map<Integer, Asistente> asistentesRegistrados = new HashMap<>();
    private static Set<Integer> asistentesDentro = new HashSet<>();

    public boolean registrarAsistente(AsistenteDTO dto) {
        int id = Integer.parseInt(dto.getId());

        if (asistentesRegistrados.containsKey(id)) {
            return false;
        }

        TipoAsistente tipo = TipoAsistente.valueOf(dto.getTipo());
        Asistente nuevo = new Asistente(id, dto.getNombre(), tipo);

        asistentesRegistrados.put(id, nuevo);
        return true;
    }

    public EstadoEntrada registrarEntrada(int idAsistente) {
        if (!asistentesRegistrados.containsKey(idAsistente)) {
            return EstadoEntrada.NO_REGISTRADO;
        }
        if (asistentesDentro.contains(idAsistente)) {
            return EstadoEntrada.YA_DENTRO;
        }
        if (asistentesDentro.size() >= AFORO_MAXIMO) {
            return EstadoEntrada.AFORO_COMPLETO;
        }
        
        asistentesDentro.add(idAsistente);
        return EstadoEntrada.AUTORIZADO;
    }

    public EstadoSalida registrarSalida(int idAsistente) {
        if (!asistentesRegistrados.containsKey(idAsistente)) {
            return EstadoSalida.NO_REGISTRADO;
        }
        if (!asistentesDentro.contains(idAsistente)) {
            return EstadoSalida.NO_ESTA_DENTRO;
        }
        
        asistentesDentro.remove(idAsistente);
        return EstadoSalida.AUTORIZADA;
    }

    public List<ResultadoEntradaDTO> registrarEntradasMasivas(List<Integer> identificadores) {
        List<ResultadoEntradaDTO> resultados = new ArrayList<>();

        for (int id : identificadores) {
            EstadoEntrada estado = registrarEntrada(id);

            resultados.add(new ResultadoEntradaDTO(id, estado));

            if (estado == EstadoEntrada.AFORO_COMPLETO || asistentesDentro.size() >= AFORO_MAXIMO) {
                break;
            }
        }

        return resultados;
    }
    
    public ReporteDTO generarReporte() {
        int registrados = asistentesRegistrados.size();
        int aforo = asistentesDentro.size();
        int disponibles = AFORO_MAXIMO - aforo;
        double ocupacion = ((double) aforo / AFORO_MAXIMO) * 100.0;

        int alumnos = 0;
        int profesores = 0;
        int invitados = 0;

        for (int id : asistentesDentro) {
            Asistente a = asistentesRegistrados.get(id);
            if (a != null) {
                if (a.getTipo() == TipoAsistente.ALUMNO) { alumnos++; }
                if (a.getTipo() == TipoAsistente.PROFESOR) { profesores++; }
                if (a.getTipo() == TipoAsistente.INVITADO) { invitados++; }
            }
        }

        return new ReporteDTO(
            registrados,
            disponibles,
            aforo,
            alumnos,
            profesores,
            invitados,
            ocupacion
        );
    }
}

public class ControlEventoUni {

    public static void main(String[] args)
            throws Exception {

        Locale.setDefault(Locale.US);

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));

        String asistentes =
                br.readLine();

        String operaciones =
                br.readLine();


        ControlAccesoService servicio =
                new ControlAccesoServiceImpl();


        /* =================================================
           REGISTRO DE ASISTENTES
           ================================================= */

        if (asistentes != null
                && !asistentes.isBlank()
                && !asistentes.equals("-")) {

            String[] registros =
                    asistentes.split(";");


            for (String registro : registros) {

                String[] datos =
                        registro.split("\\|", 3);


                AsistenteDTO dto =
                        new AsistenteDTO(
                                datos[0].trim(),
                                datos[1].trim(),
                                datos[2].trim()
                        );


                servicio.registrarAsistente(dto);
            }
        }


        StringBuilder salida =
                new StringBuilder();


        /* =================================================
           OPERACIONES
           ================================================= */

        if (operaciones != null
                && !operaciones.isBlank()
                && !operaciones.equals("-")) {

            String[] lista =
                    operaciones.split(";");


            for (String operacion : lista) {

                String[] datos =
                        operacion.split("\\|", 2);

                String accion =
                        datos[0].trim();


                /* =========================================
                   ENTRADA
                   ========================================= */

                if (accion.equals("ENTRADA")) {

                    int id =
                            Integer.parseInt(
                                    datos[1].trim());


                    EstadoEntrada estado =
                            servicio
                            .registrarEntrada(id);


                    salida.append("ENTRADA ")
                            .append(id)
                            .append(" ")
                            .append(estado)
                            .append("\n");
                }


                /* =========================================
                   SALIDA
                   ========================================= */

                else if (accion.equals("SALIDA")) {

                    int id =
                            Integer.parseInt(
                                    datos[1].trim());


                    EstadoSalida estado =
                            servicio
                            .registrarSalida(id);


                    salida.append("SALIDA ")
                            .append(id)
                            .append(" ")
                            .append(estado)
                            .append("\n");
                }


                /* =========================================
                   ENTRADA MASIVA
                   ========================================= */

                else if (accion.equals("MASIVA")) {

                    String[] ids =
                            datos[1].split(",");


                    List<Integer> identificadores =
                            new ArrayList<>();


                    for (String id : ids) {

                        identificadores.add(
                                Integer.parseInt(
                                        id.trim()));
                    }


                    List<ResultadoEntradaDTO>
                            resultados =
                            servicio
                            .registrarEntradasMasivas(
                                    identificadores);


                    if (resultados == null) {

                        throw new IllegalStateException(
                                "registrarEntradasMasivas "
                                + "no debe regresar null");
                    }


                    for (ResultadoEntradaDTO resultado
                            : resultados) {

                        salida.append("ENTRADA ")
                                .append(
                                    resultado
                                    .getIdAsistente())
                                .append(" ")
                                .append(
                                    resultado
                                    .getEstado())
                                .append("\n");
                    }
                }
            }
        }


        /* =================================================
           REPORTE
           ================================================= */

        ReporteDTO reporte =
                servicio.generarReporte();


        if (reporte == null) {

            throw new IllegalStateException(
                    "generarReporte no debe regresar null");
        }


        salida.append(
                String.format(
                        "REPORTE %d %d %d %d %d %d %.2f",
                        reporte.getRegistrados(),
                        reporte.getDisponibles(),
                        reporte.getAforo(),
                        reporte.getAlumnos(),
                        reporte.getProfesores(),
                        reporte.getInvitados(),
                        reporte.getOcupacion()
                )
        );


        System.out.print(
                salida.toString());
    }
}
