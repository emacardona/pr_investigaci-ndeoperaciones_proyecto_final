package app.ui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    public App() {
        setTitle("Optimizador Lineal - Métodos de Programación Lineal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Optimizador Lineal", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel botonesPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton btnGrafico = new JButton("Método Gráfico");
        JButton btnDual = new JButton("Método Dual");
        JButton btnPrimalDual = new JButton("Método Primal-Dual");

        botonesPanel.add(btnGrafico);
        botonesPanel.add(btnDual);
        botonesPanel.add(btnPrimalDual);
        add(botonesPanel, BorderLayout.CENTER);

        btnGrafico.addActionListener(e -> new MetodoGraficoForm().setVisible(true));
        btnDual.addActionListener(e -> new MetodoDualForm().setVisible(true));
        btnPrimalDual.addActionListener(e -> new MetodoPrimalDualForm().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}