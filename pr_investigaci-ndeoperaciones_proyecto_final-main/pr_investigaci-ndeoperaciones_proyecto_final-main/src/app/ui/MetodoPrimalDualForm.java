package app.ui;

import app.model.*;
import util.PrimalDualSolver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MetodoPrimalDualForm extends JFrame {

    private JTextField txtZ1, txtZ2;
    private JTextArea txtRestricciones;
    private JTextArea txtResultado;
    private JComboBox<String> tipoOpt;

    public MetodoPrimalDualForm() {
        setTitle("Método Primal-Dual");
        setSize(650, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ============ PANEL SUPERIOR: Función Objetivo ============
        JPanel sup = new JPanel(new GridLayout(3, 2, 5, 5));
        sup.setBorder(BorderFactory.createTitledBorder("Función Objetivo"));

        tipoOpt = new JComboBox<>(new String[]{"Maximizar", "Minimizar"});
        txtZ1 = new JTextField("3");
        txtZ2 = new JTextField("5");

        sup.add(new JLabel("Tipo:"));
        sup.add(tipoOpt);
        sup.add(new JLabel("Coeficiente de x1:"));
        sup.add(txtZ1);
        sup.add(new JLabel("Coeficiente de x2:"));
        sup.add(txtZ2);
        add(sup, BorderLayout.NORTH);

        // ============ PANEL CENTRAL: Restricciones ============
        JPanel central = new JPanel(new BorderLayout(5, 5));
        central.setBorder(BorderFactory.createTitledBorder("Restricciones"));

        JLabel lblInfo = new JLabel("<html>Ingrese las restricciones (una por línea)<br>" +
                "Formato: a1 a2 signo valor<br>" +
                "Ejemplo: 1 0 <= 4<br>" +
                "Signos válidos: <=, >=, =</html>");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        central.add(lblInfo, BorderLayout.NORTH);

        txtRestricciones = new JTextArea(8, 40);
        txtRestricciones.setText("1 0 <= 4\n0 2 <= 12\n3 2 <= 18");
        txtRestricciones.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollRest = new JScrollPane(txtRestricciones);
        central.add(scrollRest, BorderLayout.CENTER);

        add(central, BorderLayout.CENTER);

        // ============ PANEL INFERIOR: Botón y Resultados ============
        JPanel inf = new JPanel(new BorderLayout(5, 5));

        JButton btnResolver = new JButton("Resolver con Primal-Dual");
        btnResolver.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnResolver.setBackground(new Color(70, 130, 180));
        btnResolver.setForeground(Color.WHITE);
        btnResolver.setFocusPainted(false);
        inf.add(btnResolver, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setEditable(false);
        txtResultado.setBorder(BorderFactory.createTitledBorder("Solución"));
        JScrollPane scrollRes = new JScrollPane(txtResultado);
        inf.add(scrollRes, BorderLayout.CENTER);

        add(inf, BorderLayout.SOUTH);

        // ============ ACCIÓN DEL BOTÓN ============
        btnResolver.addActionListener(e -> resolver());
    }

    private void resolver() {
        try {
            // Leer función objetivo
            double z1 = Double.parseDouble(txtZ1.getText().trim());
            double z2 = Double.parseDouble(txtZ2.getText().trim());
            boolean max = tipoOpt.getSelectedItem().toString().equals("Maximizar");

            FuncionObjetivo fo = new FuncionObjetivo(new double[]{z1, z2}, max);

            // Leer restricciones
            List<Restriccion> restric = new ArrayList<>();
            String[] lineas = txtRestricciones.getText().split("\n");

            for (String linea : lineas) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split("\\s+");
                if (partes.length < 4) {
                    throw new IllegalArgumentException("Formato incorrecto en restricción: " + linea);
                }

                double a1 = Double.parseDouble(partes[0]);
                double a2 = Double.parseDouble(partes[1]);
                String signo = partes[2];
                double valor = Double.parseDouble(partes[3]);

                // Validar signo
                if (!signo.equals("<=") && !signo.equals(">=") && !signo.equals("=")) {
                    throw new IllegalArgumentException("Signo inválido: " + signo + ". Use <=, >= o =");
                }

                restric.add(new Restriccion(new double[]{a1, a2}, signo, valor));
            }

            if (restric.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar al menos una restricción");
            }

            // Resolver con Primal-Dual
            PrimalDualSolver solver = new PrimalDualSolver();
            Resultado res = solver.resolver(fo, restric.toArray(new Restriccion[0]));

            // Mostrar resultado detallado
            StringBuilder sb = new StringBuilder();
            sb.append("╔═══════════════════════════════════════════╗\n");
            sb.append("║   SOLUCIÓN MÉTODO PRIMAL-DUAL            ║\n");
            sb.append("╚═══════════════════════════════════════════╝\n\n");

            sb.append("Problema Original:\n");
            sb.append(String.format("  %s Z = %.2fx₁ + %.2fx₂\n\n",
                    max ? "MAX" : "MIN", z1, z2));

            sb.append("Restricciones:\n");
            for (int i = 0; i < restric.size(); i++) {
                Restriccion r = restric.get(i);
                sb.append(String.format("  R%d: %.2fx₁ + %.2fx₂ %s %.2f\n",
                        i + 1,
                        r.getCoeficientes()[0],
                        r.getCoeficientes()[1],
                        r.getSigno(),
                        r.getValor()));
            }

            sb.append("\n───────────────────────────────────────────\n\n");
            sb.append("SOLUCIÓN ÓPTIMA:\n");
            double[] vars = res.getVariables();
            for (int i = 0; i < vars.length; i++) {
                sb.append(String.format("  x%d = %.4f\n", i + 1, vars[i]));
            }

            sb.append(String.format("\n  Z* = %.4f", res.getValorOptimo()));

            sb.append("\n\n───────────────────────────────────────────\n");
            sb.append("Verificación de restricciones:\n");
            boolean factible = true;
            for (int i = 0; i < restric.size(); i++) {
                Restriccion r = restric.get(i);
                double izq = 0;
                for (int j = 0; j < vars.length; j++) {
                    izq += r.getCoeficientes()[j] * vars[j];
                }

                boolean cumple = false;
                String signo = r.getSigno();
                double der = r.getValor();

                if (signo.equals("<=")) cumple = izq <= der + 1e-6;
                else if (signo.equals(">=")) cumple = izq >= der - 1e-6;
                else if (signo.equals("=")) cumple = Math.abs(izq - der) < 1e-6;

                sb.append(String.format("  R%d: %.4f %s %.4f ... %s\n",
                        i + 1, izq, signo, der, cumple ? "✓" : "✗"));

                if (!cumple) factible = false;
            }

            if (factible) {
                sb.append("\n✓ La solución es factible\n");
            } else {
                sb.append("\n✗ ADVERTENCIA: La solución viola alguna restricción\n");
            }

            txtResultado.setText(sb.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: Ingrese valores numéricos válidos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error en datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage() + "\n" +
                            "Verifique que los datos sean correctos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}