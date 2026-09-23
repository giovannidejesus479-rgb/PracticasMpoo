import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static String cifrarMensaje(String mensaje, String alfabetoInterior, int posicionInicial, int intervaloRotacion) {
        StringBuilder resultado = new StringBuilder();
        mensaje = mensaje.toUpperCase();
        alfabetoInterior = alfabetoInterior.toUpperCase();
        int desplazamiento = posicionInicial;
        int letrasProcesadas = 0;
        
        for (int i = 0; i < mensaje.length(); i++) {
            char caracterActual = mensaje.charAt(i);
            
            if (caracterActual == ' ') {
                resultado.append(' ');
            } else if (caracterActual >= 'A' && caracterActual <= 'Z') {
                int posExterior = caracterActual - 'A';
                int posInterior = ((posExterior - desplazamiento) % 26 + 26) % 26;
                
                resultado.append(alfabetoInterior.charAt(posInterior));
                
                letrasProcesadas++;
                
                if (letrasProcesadas % intervaloRotacion == 0) {
                    desplazamiento = (desplazamiento + 1) % 26;
                }
            }
        }
        
        return resultado.toString();
    }
}

public class Mensajero {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Ingrese el mensaje: ");
        String mensaje = bufferedReader.readLine();

        System.out.print("Ingrese el alfabeto interior (26 letras): ");
        String alfabetoInterior = bufferedReader.readLine();

        System.out.print("Ingrese la posición inicial (entero): ");
        int posicionInicial = Integer.parseInt(bufferedReader.readLine().trim());

        System.out.print("Ingrese el intervalo de rotación (entero): ");
        int intervaloRotacion = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.cifrarMensaje(mensaje, alfabetoInterior, posicionInicial, intervaloRotacion);

        System.out.println(result);

        bufferedReader.close();
    }
}