package util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GraficoSolver extends JPanel {
    private double[][] restricciones;
    private double[] valores;
    private double[] puntoOptimo;

    public GraficoSolver(double[][] restricciones, double[] valores, double[] puntoOptimo) {
        this.restricciones = restricciones;
        this.valores = valores;
        this.puntoOptimo = puntoOptimo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        // Ejes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(50, h - 50, w - 50, h - 50);
        g2.drawLine(50, 50, 50, h - 50);

        g2.setColor(Color.BLUE);
        for (int i = 0; i < restricciones.length; i++) {
            double a = restricciones[i][0];
            double b = restricciones[i][1];
            double c = valores[i];

            double x1 = 0;
            double y1 = c / b;
            double x2 = c / a;
            double y2 = 0;

            int X1 = (int) (50 + x1 * 40);
            int Y1 = (int) (h - 50 - y1 * 40);
            int X2 = (int) (50 + x2 * 40);
            int Y2 = (int) (h - 50 - y2 * 40);

            g2.draw(new Line2D.Double(X1, Y1, X2, Y2));
        }

        // Punto óptimo
        if (puntoOptimo != null) {
            g2.setColor(Color.RED);
            int x = (int) (50 + puntoOptimo[0] * 40);
            int y = (int) (h - 50 - puntoOptimo[1] * 40);
            g2.fill(new Ellipse2D.Double(x - 5, y - 5, 10, 10));
            g2.drawString("Óptimo", x + 10, y);
        }
    }
}