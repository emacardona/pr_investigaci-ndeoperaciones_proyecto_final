package util;

import app.model.*;

public class PrimalDualSolver {

    private static final double EPSILON = 1e-10;

    public Resultado resolver(FuncionObjetivo fo, Restriccion[] restricciones) {
        try {
            int n = fo.getCoeficientes().length;
            int m = restricciones.length;

            // Construir tabla Simplex
            // Formato: [restricciones | variables holgura | RHS]
            //          [función obj  | 0s               | Z  ]

            double[][] tabla = new double[m + 1][n + m + 1];

            // Llenar restricciones
            for (int i = 0; i < m; i++) {
                // Variables originales
                for (int j = 0; j < n; j++) {
                    tabla[i][j] = restricciones[i].getCoeficientes()[j];
                }
                // Variable de holgura
                tabla[i][n + i] = 1.0;
                // Lado derecho
                tabla[i][n + m] = restricciones[i].getValor();
            }

            // Función objetivo en la última fila
            for (int j = 0; j < n; j++) {
                if (fo.isMaximizar()) {
                    tabla[m][j] = -fo.getCoeficientes()[j]; // Para MAX
                } else {
                    tabla[m][j] = fo.getCoeficientes()[j];  // Para MIN
                }
            }

            // Resolver usando Simplex
            if (!simplex(tabla, m, n + m)) {
                System.err.println("Simplex no convergió");
                return new Resultado(new double[n], 0.0);
            }

            // Extraer solución
            double[] solucion = new double[n];

            // Encontrar variables básicas
            for (int j = 0; j < n; j++) {
                // Verificar si la columna j es una columna básica
                int conteoUnos = 0;
                int filaUno = -1;
                boolean esBasica = true;

                for (int i = 0; i < m; i++) {
                    if (Math.abs(tabla[i][j] - 1.0) < EPSILON) {
                        conteoUnos++;
                        filaUno = i;
                    } else if (Math.abs(tabla[i][j]) > EPSILON) {
                        esBasica = false;
                        break;
                    }
                }

                if (esBasica && conteoUnos == 1 && filaUno >= 0) {
                    solucion[j] = tabla[filaUno][n + m];
                }
            }

            // Calcular valor óptimo
            double valorOptimo = 0;
            for (int j = 0; j < n; j++) {
                valorOptimo += fo.getCoeficientes()[j] * solucion[j];
            }

            return new Resultado(solucion, valorOptimo);

        } catch (Exception e) {
            System.err.println("Error en PrimalDualSolver: " + e.getMessage());
            e.printStackTrace();
            return new Resultado(new double[fo.getCoeficientes().length], 0.0);
        }
    }

    private boolean simplex(double[][] tabla, int m, int n) {
        int maxIteraciones = 100;

        for (int iter = 0; iter < maxIteraciones; iter++) {

            // Paso 1: Encontrar columna pivote (variable que entra)
            int colPivote = -1;
            double valorMasNegativo = -EPSILON;

            for (int j = 0; j < n; j++) {
                if (tabla[m][j] < valorMasNegativo) {
                    valorMasNegativo = tabla[m][j];
                    colPivote = j;
                }
            }

            // Si no hay valores negativos, hemos terminado (óptimo)
            if (colPivote == -1) {
                return true;
            }

            // Paso 2: Encontrar fila pivote (variable que sale) - Regla del cociente mínimo
            int filaPivote = -1;
            double cocienteMinimo = Double.POSITIVE_INFINITY;

            for (int i = 0; i < m; i++) {
                if (tabla[i][colPivote] > EPSILON) {
                    double cociente = tabla[i][n] / tabla[i][colPivote];
                    if (cociente >= 0 && cociente < cocienteMinimo) {
                        cocienteMinimo = cociente;
                        filaPivote = i;
                    }
                }
            }

            // Si no se encuentra fila pivote, el problema es no acotado
            if (filaPivote == -1) {
                System.err.println("Problema no acotado");
                return false;
            }

            // Paso 3: Operación de pivoteo
            double elementoPivote = tabla[filaPivote][colPivote];

            // Normalizar fila pivote
            for (int j = 0; j <= n; j++) {
                tabla[filaPivote][j] /= elementoPivote;
            }

            // Hacer ceros en la columna pivote excepto en la fila pivote
            for (int i = 0; i <= m; i++) {
                if (i != filaPivote) {
                    double factor = tabla[i][colPivote];
                    for (int j = 0; j <= n; j++) {
                        tabla[i][j] -= factor * tabla[filaPivote][j];
                    }
                }
            }
        }

        System.err.println("Se alcanzó el límite de iteraciones");
        return true; // Retornar la mejor solución encontrada
    }
}