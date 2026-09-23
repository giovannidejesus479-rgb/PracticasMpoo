import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static String evaluarLicencia(String fechaActual, String fechaVencimiento, String tipoLicencia, int renovacionesPrevias) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            tipoLicencia = tipoLicencia.toUpperCase(); //convierte todo a mayÚsculas para que coincida con las opciones
            
            Date dateActual = sdf.parse(fechaActual);
            Date dateVencimiento = sdf.parse(fechaVencimiento);
            
            long diffMillis = dateVencimiento.getTime() - dateActual.getTime();
            long diasRest = diffMillis / (1000 * 60 * 60 * 24); //la multiplicación corresponde al número de milisegundos que hay en un día, al dividir entre esa cantidad te devuelve el número exacto de días
                        
            String estado;
            if (diasRest > 30) {
                estado = "VIGENTE";
            } else if (diasRest >= 0){
                estado = "PROXIMA_A_VENCER";
            } else if (diasRest >= -90){
                estado = "VENCIDA";
            } else {
                estado = "BLOQUEADA";
            }
            
            if (estado.equals("BLOQUEADA")){
                return "BLOQUEADA " + diasRest + " 0.00 NO_DISPONIBLE"; //se deben respetar los espacio después de bloqueada y antes de 0.00 de lo contrario, al concatenar se produce un fallo en el formato y nos arroja un error
            }
            
            double costoBase = 0.0;
            int addYear = 0;
            int addMonth = 0;
            
            switch (tipoLicencia){
                case "BASICA":
                    costoBase = 1000.00;
                    addYear = 1;
                    break;
                case "PROFESIONAL":
                    costoBase = 1500.00;
                    addYear = 2;
                    break;
                case "EMPRESARIAL":
                    costoBase = 2500.00;
                    addYear = 3;
                    break;
                case "TEMPORAL":
                    costoBase = 600.00;
                    addMonth = 6;
                    break;
                default:
                    return "ERROR: TIPO_LICENCIA_NO_RECONOCIDO"; //agregamos un default para opciones no validas
            }
            
            double costoFinal = costoBase;
            if (estado.equals("VIGENTE")){
                costoFinal *= 0.90;
            } else if (estado.equals("VENCIDA")){
                costoFinal *= 1.20;
            }
            
            if (renovacionesPrevias > 3){
                costoFinal *= 0.95;
            }
            
            Calendar nuevaFechaCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            if (estado.equals("VIGENTE") || estado.equals("PROXIMA_A_VENCER")) {
                nuevaFechaCal.setTime(dateVencimiento);
            } else {
                nuevaFechaCal.setTime(dateActual);
            }
            
            if (addYear > 0){
                nuevaFechaCal.add(Calendar.YEAR, addYear);
            }
            
            if (addMonth > 0){
                nuevaFechaCal.add(Calendar.MONTH, addMonth);
            }
            
            String nuevaFechaStr = sdf.format(nuevaFechaCal.getTime());
            String costoStr = String.format(Locale.US, "%.2f", costoFinal);
            
            return estado + " " + diasRest + " " + costoStr + " " + nuevaFechaStr;
            
        } catch (ParseException e){
            System.out.println("Error procesando las fechas. Asegúrate de usar el formato dd/MM/yyyy o ddMMyyyy.");
            return "ERROR_FECHA";
        }
    }
}

public class Licencia {

    //agregamos la siguiente función para facilitar al usuario el ingreso de la fecha, es decir, basta con que ingrese los 8 digitos de la fecha y la función agregará las diagonales correspondientes de acuerdo al formato dd/MM/yyyy
    private static String adaptarFecha(String fecha) {
        if (fecha == null) return "";
        fecha = fecha.trim(); //limpia los espacios antes y después de la cadena para evitar errores de formato

        if (fecha.length() == 8 && !fecha.contains("/")) {
            String dia = fecha.substring(0, 2); //substring divide la cadena ingresada en cadenas mas pequeñas de acuerdo a los indices marcados y para poder agregar "/" y de esta forma lograr que coincida el fromato
            String mes = fecha.substring(2, 4);
            String year = fecha.substring(4, 8);
            return dia + "/" + mes + "/" + year;
        }
        return fecha;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        //el usuario ingresa los datos solicitados
        System.out.print("Fecha Actual (DD/MM/YYYY):\n ");
        String fechaActual = adaptarFecha(bufferedReader.readLine());

        System.out.print("Fecha Vencimiento (DD/MM/YYYY):\n ");
        String fechaVencimiento = adaptarFecha(bufferedReader.readLine());

        System.out.print("Tipo Licencia (BASICA, PROFESIONAL, EMPRESARIAL, TEMPORAL):\n ");
        // .toUpperCase() convierte toda la entrada a mayúsculas automáticamente
        String tipoLicencia = bufferedReader.readLine().trim().toUpperCase();

        System.out.print("Renovaciones previas:\n ");
        int renovacionesPrevias = Integer.parseInt(bufferedReader.readLine().trim());

        System.out.println("\nProcesando...");
        
        String result = Result.evaluarLicencia(
                fechaActual,
                fechaVencimiento,
                tipoLicencia,
                renovacionesPrevias
        );

        System.out.println("Resultado: " + result);

        bufferedReader.close();
    }
}