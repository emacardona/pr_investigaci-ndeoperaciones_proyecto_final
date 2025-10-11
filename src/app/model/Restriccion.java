package app.model;

public class Restriccion {
    private double[] coeficientes;
    private double valor;
    private String signo;

    public Restriccion(double[] coeficientes, String signo, double valor) {
        this.coeficientes = coeficientes;
        this.signo = signo;
        this.valor = valor;
    }

    public double[] getCoeficientes() { return coeficientes; }
    public String getSigno() { return signo; }
    public double getValor() { return valor; }
}