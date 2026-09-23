import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'detectarZonaAjuste' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts INTEGER_ARRAY vagones as parameter.
     */

    public static int[] detectarZonaAjuste(int[] vagones) {
        int n = vagones.length;
        
        int[] vagOrdenados = vagones.clone();
        Arrays.sort(vagOrdenados);
        
        int inicio = -1;
        int fin = -1;
        
        for (int i = 0; i < n; i++) {
            if (vagones[i] != vagOrdenados[i]) {
                if (inicio == -1) {
                    inicio = i;
                }
                fin = i;
            }
        }
        
        if (inicio == -1) {
            return new int[]{-1, -1, 0};
        }
        
        int tam = fin - inicio + 1;

        return new int[]{inicio, fin, tam};
    }
}

public class Vagones {

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Ingresa los números de los vagones separados por espacio:");
        String linea = bufferedReader.readLine();

        // Validar que la entrada no esté vacía
        if (linea == null || linea.trim().isEmpty()) {
            System.out.println("No se ingresaron datos.");
            return;
        }

        String[] datos = linea.trim().split("\\s+");
        int[] vagones = new int[datos.length];

        for (int i = 0; i < datos.length; i++) {
            vagones[i] = Integer.parseInt(datos[i]);
        }

        int[] result = Result.detectarZonaAjuste(vagones);

        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i]);
            if (i != result.length - 1) {
                System.out.print(" ");
            }
        }
        
        System.out.println(); // Salto de línea final
        bufferedReader.close();
    }
}