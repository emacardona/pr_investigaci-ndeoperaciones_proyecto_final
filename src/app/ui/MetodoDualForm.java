package app.ui;

import app.model.*;
import util.SimplexSolver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MetodoDualForm extends JFrame {

    private JTextField txtZ1, txtZ2;
    private JTextArea txtRestricciones;
    private JTextArea txtResultado;
    private JComboBox<String> tipoOpt;

    public MetodoDualForm() {
        setTitle("Método Dual");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel sup = new JPanel(new GridLayout(3, 2, 5, 5));
        sup.setBorder(BorderFactory.createTitledBorder("Función Objetivo"));

        tipoOpt = new JComboBox<>(new String[]{"Maximizar", "Minimizar"});
        txtZ1 = new JTextField();
        txtZ2 = new JTextField();

        sup.add(new JLabel("Tipo:"));
        sup.add(tipoOpt);
        sup.add(new JLabel("Coeficiente de x1:"));
        sup.add(txtZ1);
        sup.add(new JLabel("Coeficiente de x2:"));
        sup.add(txtZ2);
        add(sup, BorderLayout.NORTH);

        txtRestricciones = new JTextArea(5, 40);
        txtRestricciones.setText("1 0 <= 4\n0 2 <= 12\n3 2 <= 18");
        add(new JScrollPane(txtRestricciones), BorderLayout.CENTER);

        JPanel inf = new JPanel(new BorderLayout());
        JButton btn = new JButton("Resolver");
        txtResultado = new JTextArea();
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setEditable(false);

        inf.add(btn, BorderLayout.NORTH);
        inf.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        add(inf, BorderLayout.SOUTH);

        btn.addActionListener(e -> resolver());
    }

    private void resolver() {
        try {
            double z1 = Double.parseDouble(txtZ1.getText());
            double z2 = Double.parseDouble(txtZ2.getText());
            boolean max = tipoOpt.getSelectedItem().toString().equals("Maximizar");

            FuncionObjetivo fo = new FuncionObjetivo(new double[]{z1, z2}, max);

            List<Restriccion> restric = new ArrayList<>();
            for (String linea : txtRestricciones.getText().split("\n")) {
                String[] p = linea.trim().split(" ");
                double a = Double.parseDouble(p[0]);
                double b = Double.parseDouble(p[1]);
                String signo = p[2];
                double val = Double.parseDouble(p[3]);
                restric.add(new Restriccion(new double[]{a, b}, signo, val));
            }

            SimplexSolver solver = new SimplexSolver();
            Resultado res = solver.resolverSimplex(fo, restric.toArray(new Restriccion[0]));
            txtResultado.setText(res.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}