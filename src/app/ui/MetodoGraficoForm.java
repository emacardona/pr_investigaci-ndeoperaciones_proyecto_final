package app.ui;

import util.GraficoSolver;
import javax.swing.*;
import java.awt.*;

public class MetodoGraficoForm extends JFrame {

    public MetodoGraficoForm() {
        setTitle("Método Gráfico");
        setSize(600, 600);
        setLocationRelativeTo(null);

        double[][] restricciones = {
                {2, 1},
                {1, 2},
                {1, 0}
        };
        double[] valores = {8, 10, 5};
        double[] puntoOptimo = {2, 3};

        GraficoSolver panel = new GraficoSolver(restricciones, valores, puntoOptimo);
        add(panel, BorderLayout.CENTER);
    }
}