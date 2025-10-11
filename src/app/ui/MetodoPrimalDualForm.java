package app.ui;

import app.model.*;
import util.PrimalDualSolver;

import javax.swing.*;
import java.awt.*;

public class MetodoPrimalDualForm extends JFrame {

    public MetodoPrimalDualForm() {
        setTitle("Método Primal-Dual");
        setSize(500, 400);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));

        FuncionObjetivo fo = new FuncionObjetivo(new double[]{2, 3}, true);
        Restriccion[] rest = {
                new Restriccion(new double[]{1, 1}, "<=", 4),
                new Restriccion(new double[]{1, 2}, "<=", 6)
        };

        PrimalDualSolver solver = new PrimalDualSolver();
        Resultado r = solver.resolver(fo, rest);

        area.setText(r.toString());
        add(new JScrollPane(area));
    }
}