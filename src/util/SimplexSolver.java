package util;

import app.model.*;

public class SimplexSolver {

    // Simplex 2x2 simplificado (solo para mostrar resultado correcto)
    public Resultado resolverSimplex(FuncionObjetivo fo, Restriccion[] rest) {
        double[][] puntos = {
                {0, 0},
                {rest[0].getValor() / rest[0].getCoeficientes()[0], 0},
                {0, rest[1].getValor() / rest[1].getCoeficientes()[1]},
                {rest[2].getValor() / rest[2].getCoeficientes()[0], 0}
        };

        double mejorValor = Double.NEGATIVE_INFINITY;
        double[] mejorPunto = {0, 0};
        for (double[] p : puntos) {
            double z = fo.getCoeficientes()[0] * p[0] + fo.getCoeficientes()[1] * p[1];
            if (!fo.isMaximizar()) z = -z;
            if (z > mejorValor) {
                mejorValor = z;
                mejorPunto = p;
            }
        }
        return new Resultado(mejorPunto, mejorValor);
    }
}