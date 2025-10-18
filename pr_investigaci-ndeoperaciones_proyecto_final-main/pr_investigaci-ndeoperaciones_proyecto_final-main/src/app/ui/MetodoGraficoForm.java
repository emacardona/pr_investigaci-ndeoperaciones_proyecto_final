package app.ui;

import util.GraficoSolver;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MetodoGraficoForm extends JFrame {

    private JTextField txtFuncionA, txtFuncionB;
    private JCheckBox chkMaximizar;
    private JTextArea txtRestricciones;
    private JButton btnGraficar, btnLimpiar, btnExportar;
    private GraficoSolver panelGrafico;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;

    public MetodoGraficoForm() {
        setTitle("Método Gráfico - Programación Lineal");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ======== PANEL IZQUIERDO (ENTRADAS) ========
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Datos del problema"));
        panelIzquierdo.setPreferredSize(new Dimension(350, getHeight()));

        // Función objetivo
        JPanel panelFuncion = new JPanel();
        panelFuncion.add(new JLabel("Z = "));
        txtFuncionA = new JTextField("3", 4);
        txtFuncionB = new JTextField("2", 4);
        panelFuncion.add(txtFuncionA);
        panelFuncion.add(new JLabel("x +"));
        panelFuncion.add(txtFuncionB);
        panelFuncion.add(new JLabel("y"));
        panelIzquierdo.add(panelFuncion);

        // Tipo de optimización
        chkMaximizar = new JCheckBox("Maximizar", true);
        JPanel panelTipo = new JPanel();
        panelTipo.add(chkMaximizar);
        panelIzquierdo.add(panelTipo);

        // Restricciones
        panelIzquierdo.add(new JLabel("Restricciones (una por línea: a b c)"));
        txtRestricciones = new JTextArea(8, 25);
        txtRestricciones.setText("2 1 8\n1 2 10\n1 0 5");
        JScrollPane scroll = new JScrollPane(txtRestricciones);
        panelIzquierdo.add(scroll);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGraficar = new JButton("Graficar");
        btnLimpiar = new JButton("Limpiar");
        btnExportar = new JButton("Exportar Imagen");
        panelBotones.add(btnGraficar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnExportar);
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(panelBotones);
        panelIzquierdo.add(Box.createVerticalGlue());

        // ======== PANEL DERECHO (GRÁFICO Y RESULTADOS) ========
        JPanel panelDerecho = new JPanel(new BorderLayout());

        // Gráfico
        panelGrafico = new GraficoSolver();
        panelGrafico.setBorder(BorderFactory.createTitledBorder("Gráfico del problema"));
        panelDerecho.add(panelGrafico, BorderLayout.CENTER);

        // Tabla de resultados
        modeloTabla = new DefaultTableModel(new Object[]{"x", "y", "Z"}, 0);
        tablaResultados = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.setPreferredSize(new Dimension(300, 120));
        panelDerecho.add(scrollTabla, BorderLayout.SOUTH);

        // Agregar ambos paneles
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        // === ACCIONES ===
        btnGraficar.addActionListener(e -> graficar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnExportar.addActionListener(e -> exportarImagen());
    }

    private void graficar() {
        try {
            double a = Double.parseDouble(txtFuncionA.getText());
            double b = Double.parseDouble(txtFuncionB.getText());
            boolean maximizar = chkMaximizar.isSelected();

            // Leer restricciones
            String[] lineas = txtRestricciones.getText().split("\\n");
            List<double[]> rest = new ArrayList<>();
            List<Double> vals = new ArrayList<>();

            for (String linea : lineas) {
                String[] partes = linea.trim().split("\\s+");
                if (partes.length == 3) {
                    double a1 = Double.parseDouble(partes[0]);
                    double b1 = Double.parseDouble(partes[1]);
                    double c1 = Double.parseDouble(partes[2]);
                    rest.add(new double[]{a1, b1});
                    vals.add(c1);
                }
            }

            double[][] restricciones = rest.toArray(new double[0][]);
            double[] valores = vals.stream().mapToDouble(Double::doubleValue).toArray();
            double[] funcion = {a, b};

            // Actualizar gráfico y tabla
            var resultados = panelGrafico.actualizarDatos(restricciones, valores, funcion, maximizar);
            panelGrafico.repaint();

            modeloTabla.setRowCount(0);
            for (double[] fila : resultados) {
                modeloTabla.addRow(new Object[]{
                        String.format("%.2f", fila[0]),
                        String.format("%.2f", fila[1]),
                        String.format("%.2f", fila[2])
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtFuncionA.setText("");
        txtFuncionB.setText("");
        txtRestricciones.setText("");
        modeloTabla.setRowCount(0);
        panelGrafico.limpiarGrafico();
        panelGrafico.repaint();
    }

    private void exportarImagen() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar gráfica como imagen PNG");
            int opcion = chooser.showSaveDialog(this);

            if (opcion == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".png")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                }
                panelGrafico.exportarComoImagen(archivo);
                JOptionPane.showMessageDialog(this, "Imagen exportada correctamente:\n" + archivo.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar la imagen: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
