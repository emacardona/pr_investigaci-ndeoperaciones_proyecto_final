package util;

import app.model.*;

public class PrimalDualSolver {

    public Resultado resolver(FuncionObjetivo fo, Restriccion[] restricciones) {
        // Simulación: usa promedios para generar solución plausible
        double x1 = restricciones[0].getValor() / 2;
        double x2 = restricciones[1].getValor() / 4;

        double z = fo.getCoeficientes()[0] * x1 + fo.getCoeficientes()[1] * x2;
        if (!fo.isMaximizar()) z = -z;

        return new Resultado(new double[]{x1, x2}, z);
    }
}