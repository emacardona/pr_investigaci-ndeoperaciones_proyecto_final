package app.model;

public class FuncionObjetivo {
    private double[] coeficientes;
    private boolean maximizar;

    public FuncionObjetivo(double[] coeficientes, boolean maximizar) {
        this.coeficientes = coeficientes;
        this.maximizar = maximizar;
    }

    public double[] getCoeficientes() { return coeficientes; }
    public boolean isMaximizar() { return maximizar; }
}