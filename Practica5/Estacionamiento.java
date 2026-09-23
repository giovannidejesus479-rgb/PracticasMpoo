import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    //agregamos esta funcion para adaptar el formato de la fecha ingresado por el usuario
    private static String formatearFecha(String fecha) {
        if (fecha == null) return "";
        fecha = fecha.trim(); //limpia los posibles espacios al principio o al final de la cadena

        if (fecha.length() == 8 && !fecha.contains("/")) {
            String dia = fecha.substring(0, 2);
            String mes = fecha.substring(2, 4);
            String year = fecha.substring(4, 8);
            return dia + "/" + mes + "/" + year;
        }
        return fecha;
    }

    //agregamos esta función para adaptar el formato de la hora
    private static String formatearHora(String hora) {
        if (hora == null) return "";
        hora = hora.trim();
        // Si no contiene ":" procedemos a formatearlo
        if (!hora.contains(":")) {
            //si la cadena correspondiente a la hora es igual a 3 agregamos un 0 al inicio para que coincida con el formato de hora
            if (hora.length() == 3) {
                hora = "0" + hora;
            }
            if (hora.length() == 4) {
                return hora.substring(0, 2) + ":" + hora.substring(2, 4);
            }
        }
        return hora;
    }

    public static String calcularEstancia(String tipoVehiculo, String fechaEntrada, String horaEntrada, String fechaSalida, String horaSalida) {
        try {
            //Convertimos el tipo de vehículo a MAYÚSCULAS
            tipoVehiculo = tipoVehiculo != null ? tipoVehiculo.trim().toUpperCase() : "";
            
            //Adaptamos fechas y horas
            fechaEntrada = formatearFecha(fechaEntrada);
            horaEntrada = formatearHora(horaEntrada);
            fechaSalida = formatearFecha(fechaSalida);
            horaSalida = formatearHora(horaSalida);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setLenient(false);
            
            Date dEntrada = sdf.parse(fechaEntrada + " " + horaEntrada);
            Date dSalida = sdf.parse(fechaSalida + " " + horaSalida);
            
            Calendar calEntrada = Calendar.getInstance();
            calEntrada.setTime(dEntrada);
            
            Calendar calSalida = Calendar.getInstance();
            calSalida.setTime(dSalida);
            
            if (!calSalida.after(calEntrada)){
                return "INVALID";
            }
            
            long diffMillis = calSalida.getTimeInMillis() - calEntrada.getTimeInMillis();
            double diffMinutes = (double) diffMillis / (1000 * 60);
            long horasCobradas = (long) Math.ceil(diffMinutes / 60.0);//Math.ceil redondea un número decimal hacia arriba. Nota: en esta linea se realizó un cast ya que Math.ceil() devuelve un double
            
            double tarifaHora = 0;
            double tarifaMax24h = 0;
            
            switch (tipoVehiculo) {
                case "MOTOCICLETA":
                    tarifaHora = 15;
                    tarifaMax24h = 100;
                    break;
                case "AUTOMOVIL":
                    tarifaHora = 25;
                    tarifaMax24h = 180;
                    break;
                case "CAMIONETA":
                    tarifaHora = 35;
                    tarifaMax24h = 250;
                    break;
                case "ELECTRICO":
                    tarifaHora = 20;
                    tarifaMax24h = 150;
                    break;
                default: 
                    return "INVALID";
            }
            
            long bloques24h = horasCobradas / 24;
            long horasRestantes = horasCobradas % 24;
            
            double costoBase = (bloques24h * tarifaMax24h) + Math.min(horasRestantes * tarifaHora, tarifaMax24h);//Math.min compara dos numeros y devuelve el menor de ellos.
            double costoFinal = costoBase;
            
            int diaE = calEntrada.get(Calendar.DAY_OF_WEEK);
            int diaS = calSalida.get(Calendar.DAY_OF_WEEK);
            boolean esFinDeSemana = (diaE == Calendar.SATURDAY || diaE == Calendar.SUNDAY || diaS == Calendar.SATURDAY || diaS == Calendar.SUNDAY);
            
            int horaE = calEntrada.get(Calendar.HOUR_OF_DAY);
            int horaS = calSalida.get(Calendar.HOUR_OF_DAY);
            boolean fechasDiferentes = (calEntrada.get(Calendar.YEAR) != calSalida.get(Calendar.YEAR)) || (calEntrada.get(Calendar.DAY_OF_YEAR) != calSalida.get(Calendar.DAY_OF_YEAR));
            
            boolean esNocturna = (horaE >= 20) || (horaS < 6) || fechasDiferentes;
            if (esFinDeSemana) {
                costoFinal *= 1.20;
            }
                
            if (esNocturna) {
                costoFinal *= 1.15;
            }
            
            if (tipoVehiculo.equals("ELECTRICO")){
                costoFinal *= 0.90;
            }
            
            String tipoEstancia;
            if (esFinDeSemana && esNocturna) {
                tipoEstancia = "MIXTA";
            } else if (esFinDeSemana) {
                tipoEstancia = "FIN_SEMANA";
            } else if (esNocturna) {
                tipoEstancia = "NOCTURNA";
            } else {
                tipoEstancia = "NORMAL";
            }
            
            return String.format(Locale.US, "%d %.2f %s", horasCobradas, costoFinal, tipoEstancia);
            
        } catch (Exception e){
            return "INVALID";
        }
    }
}

public class Estacionamiento {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        //solicita al usuario la informacion para realizar el cobro correspondiente
        System.out.println("Ingresa tipo de vehiculo: ");
        String tipoVehiculo = bufferedReader.readLine();

        System.out.println("Ingresa fecha de entrada (DD/MM/YYYY): ");
        String fechaEntrada = bufferedReader.readLine();

        System.out.println("Ingresa hora de entrada (HH:MM): ");
        String horaEntrada = bufferedReader.readLine();

        System.out.println("Ingresa fecha de salida (DD/MM/YYYY): ");
        String fechaSalida = bufferedReader.readLine();

        System.out.println("Ingresa hora de salida (HH:MM): ");
        String horaSalida = bufferedReader.readLine();

        String result = Result.calcularEstancia(tipoVehiculo, fechaEntrada, horaEntrada, fechaSalida, horaSalida);

        System.out.println(result);

        bufferedReader.close();
    }
}