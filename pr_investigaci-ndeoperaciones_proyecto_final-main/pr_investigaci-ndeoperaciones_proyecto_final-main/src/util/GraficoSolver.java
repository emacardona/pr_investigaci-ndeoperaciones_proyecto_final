package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraficoSolver extends JPanel {

    private double[][] restricciones;
    private double[] valores;
    private double[] funcionObjetivo;
    private boolean maximizar;
    private double[] puntoOptimo;
    private List<double[]> puntosFactibles;
    private double escala = 40.0;

    public GraficoSolver() {
        setBackground(Color.WHITE);
    }

    // =================== ACTUALIZAR DATOS ===================
    public List<double[]> actualizarDatos(double[][] restricciones, double[] valores, double[] funcionObjetivo, boolean maximizar) {
        this.restricciones = restricciones;
        this.valores = valores;
        this.funcionObjetivo = funcionObjetivo;
        this.maximizar = maximizar;
        calcularOptimo();
        return obtenerTablaResultados();
    }

    // =================== LIMPIAR GRÁFICO ===================
    public void limpiarGrafico() {
        restricciones = null;
        valores = null;
        funcionObjetivo = null;
        puntosFactibles = null;
        puntoOptimo = null;
    }

    // =================== EXPORTAR COMO IMAGEN ===================
    public void exportarComoImagen(File archivo) throws IOException {
        BufferedImage imagen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();
        paint(g2);
        g2.dispose();
        ImageIO.write(imagen, "png", archivo);
    }

    // =================== CÁLCULO PRINCIPAL ===================
    private void calcularOptimo() {
        if (restricciones == null || valores == null) return;

        List<double[]> intersecciones = calcularIntersecciones();
        puntosFactibles = new ArrayList<>();

        // Guardar puntos factibles (los que cumplen todas las restricciones)
        for (double[] p : intersecciones) {
            if (esFactible(p[0], p[1])) puntosFactibles.add(p);
        }

        // Añadir el origen como punto factible básico
        puntosFactibles.add(new double[]{0, 0});

        // Calcular el punto óptimo
        puntoOptimo = calcularPuntoOptimo();
    }

    // =================== INTERSECCIONES ===================
    private List<double[]> calcularIntersecciones() {
        List<double[]> puntos = new ArrayList<>();
        for (int i = 0; i < restricciones.length; i++) {
            for (int j = i + 1; j < restricciones.length; j++) {
                double a1 = restricciones[i][0], b1 = restricciones[i][1], c1 = valores[i];
                double a2 = restricciones[j][0], b2 = restricciones[j][1], c2 = valores[j];
                double det = a1 * b2 - a2 * b1;

                // Evitar división por cero (líneas paralelas)
                if (Math.abs(det) > 1e-6) {
                    double x = (c1 * b2 - c2 * b1) / det;
                    double y = (a1 * c2 - a2 * c1) / det;
                    if (x >= 0 && y >= 0) puntos.add(new double[]{x, y});
                }
            }
        }
        return puntos;
    }

    // =================== VERIFICAR FACTIBILIDAD ===================
    private boolean esFactible(double x, double y) {
        for (int i = 0; i < restricciones.length; i++) {
            if (restricciones[i][0] * x + restricciones[i][1] * y > valores[i] + 1e-6)
                return false;
        }
        return true;
    }

    // =================== PUNTO ÓPTIMO ===================
    private double[] calcularPuntoOptimo() {
        double mejorValor = maximizar ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        double[] mejorPunto = null;

        if (puntosFactibles == null) return null;

        for (double[] p : puntosFactibles) {
            double z = funcionObjetivo[0] * p[0] + funcionObjetivo[1] * p[1];
            if ((maximizar && z > mejorValor) || (!maximizar && z < mejorValor)) {
                mejorValor = z;
                mejorPunto = p;
            }
        }
        return mejorPunto;
    }

    // =================== RESULTADOS PARA TABLA ===================
    private List<double[]> obtenerTablaResultados() {
        List<double[]> resultados = new ArrayList<>();
        if (puntosFactibles != null) {
            for (double[] p : puntosFactibles) {
                double z = funcionObjetivo[0] * p[0] + funcionObjetivo[1] * p[1];
                resultados.add(new double[]{p[0], p[1], z});
            }
        }
        return resultados;
    }

    // =================== DIBUJO DEL GRÁFICO ===================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (restricciones == null || valores == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();

        // ---------- Ejes ----------
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(50, h - 50, w - 50, h - 50); // Eje X
        g2.drawLine(50, 50, 50, h - 50);         // Eje Y
        g2.drawString("X", w - 40, h - 55);
        g2.drawString("Y", 55, 65);

        // ---------- Restricciones (líneas azules) ----------
        g2.setColor(Color.BLUE);
        for (int i = 0; i < restricciones.length; i++) {
            double a = restricciones[i][0];
            double b = restricciones[i][1];
            double c = valores[i];

            double x1 = 0, y1 = c / b;
            double x2 = c / a, y2 = 0;

            int X1 = (int) (50 + x1 * escala);
            int Y1 = (int) (h - 50 - y1 * escala);
            int X2 = (int) (50 + x2 * escala);
            int Y2 = (int) (h - 50 - y2 * escala);

            g2.draw(new Line2D.Double(X1, Y1, X2, Y2));
        }

        // ---------- Región factible (ahora siempre visible) ----------
        if (puntosFactibles != null && puntosFactibles.size() >= 2) {
            g2.setColor(new Color(0, 200, 0, 70));
            Polygon region = new Polygon();

            for (double[] p : puntosFactibles) {
                int X = (int) (50 + p[0] * escala);
                int Y = (int) (h - 50 - p[1] * escala);
                region.addPoint(X, Y);
            }

            if (puntosFactibles.size() >= 3)
                g2.fill(region);     // Rellenar el área
            else
                g2.draw(region);     // Dibujar contorno si solo hay 2 puntos

            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Región Factible", 60, 80);
        }

        // ---------- Punto óptimo ----------
        if (puntoOptimo != null) {
            g2.setColor(Color.RED);
            int x = (int) (50 + puntoOptimo[0] * escala);
            int y = (int) (h - 50 - puntoOptimo[1] * escala);

            g2.fill(new Ellipse2D.Double(x - 5, y - 5, 10, 10));
            g2.drawString(
                    String.format("Óptimo (%.2f, %.2f)  Z=%.2f",
                            puntoOptimo[0], puntoOptimo[1],
                            funcionObjetivo[0] * puntoOptimo[0] + funcionObjetivo[1] * puntoOptimo[1]),
                    x + 10, y
            );
        }
    }
}
