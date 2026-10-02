import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    static class PilaDinamica {

        private double[] elementos;
        private int cima;

        public PilaDinamica(int capacidadInicial) {
            this.elementos = new double[capacidadInicial];
            this.cima = capacidadInicial - 1;
        }

        public void push(double valor) {
            if (this.cima < 0) {
                ampliarCapacidad();
            }
            this.elementos[this.cima] = valor;
            this.cima--;
        }

        public double pop() {
            if (isEmpty()) {
                return Double.NaN;
            }
            this.cima++;
            double valor = this.elementos[this.cima];
            this.elementos[this.cima] = Double.NaN;
            return valor;
        }

        public double peek() {
            if (isEmpty()) {
                return Double.NaN;
            }
            return this.elementos[this.cima + 1];
        }

        public boolean isEmpty() {
            return this.cima == this.elementos.length - 1;
        }

        public int size() {
            return this.elementos.length - 1 - this.cima;
        }

        public int capacity() {
            return this.elementos.length;
        }

        private void ampliarCapacidad() {
            int capacidadAnterior = this.elementos.length;
            int nuevaCapacidad = capacidadAnterior * 2;
            double[] nuevoArreglo = new double[nuevaCapacidad];
            Arrays.fill(nuevoArreglo, 0, capacidadAnterior, Double.NaN);
            System.arraycopy(this.elementos, 0, nuevoArreglo, capacidadAnterior, capacidadAnterior);
            
            this.elementos = nuevoArreglo;
            this.cima = capacidadAnterior - 1;
        }
    }

    /*
     * NO MODIFICAR. Reservado para HackerRank
     */
    public static List<String> procesarOperaciones(
            int capacidad, List<String> operaciones) {

        PilaDinamica pila = new PilaDinamica(capacidad);
        List<String> salida = new ArrayList<>();

        for (String linea : operaciones) {

            String[] partes = linea.trim().split("\\s+");
            String operacion = partes[0];

            switch (operacion) {

                case "PUSH":
                    double valor = Double.parseDouble(partes[1]);
                    pila.push(valor);
                    salida.add("OK");
                    break;

                case "POP":
                    double eliminado = pila.pop();
                    if (Double.isNaN(eliminado)) {
                        salida.add("EMPTY");
                    } else {
                        salida.add(String.valueOf(eliminado));
                    }
                    break;

                case "PEEK":
                    double cima = pila.peek();
                    if (Double.isNaN(cima)) {
                        salida.add("EMPTY");
                    } else {
                        salida.add(String.valueOf(cima));
                    }
                    break;

                case "SIZE":
                    salida.add(String.valueOf(pila.size()));
                    break;

                case "CAPACITY":
                    salida.add(String.valueOf(pila.capacity()));
                    break;

                case "ISEMPTY":
                    salida.add(String.valueOf(pila.isEmpty()));
                    break;
            }
        }

        return salida;
    }
}

public class Pila {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("--- Ingrese Capacidad Inicial ---");
        String capacidadLine = bufferedReader.readLine();
        if (capacidadLine == null) return;
        int capacidad = Integer.parseInt(capacidadLine.trim());

        System.out.println("--- Ingrese Número de Operaciones ---");
        String nOperacionesLine = bufferedReader.readLine();
        if (nOperacionesLine == null) return;
        int nOperaciones = Integer.parseInt(nOperacionesLine.trim());

        System.out.println("--- Ingrese las " + nOperaciones + " Operaciones (una por línea) ---");
        List<String> operaciones = new ArrayList<>();

        for (int i = 0; i < nOperaciones; i++) {
            String operacionesItem = bufferedReader.readLine();
            operaciones.add(operacionesItem);
        }

        // Procesar
        List<String> result = Result.procesarOperaciones(capacidad, operaciones);

        System.out.println("\n--- RESULTADOS ---");
        for (String res : result) {
            System.out.println(res);
        }

        bufferedReader.close();
    }
}