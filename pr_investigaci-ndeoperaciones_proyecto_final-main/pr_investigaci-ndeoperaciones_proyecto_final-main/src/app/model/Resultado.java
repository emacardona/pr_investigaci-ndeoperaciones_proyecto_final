package app.model;

public class Resultado {
    private double[] variables;
    private double valorOptimo;

    public Resultado(double[] variables, double valorOptimo) {
        this.variables = variables;
        this.valorOptimo = valorOptimo;
    }

    public double[] getVariables() { return variables; }
    public double getValorOptimo() { return valorOptimo; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Resultado:\n");
        for (int i = 0; i < variables.length; i++) {
            sb.append("x").append(i + 1).append(" = ")
                    .append(String.format("%.2f", variables[i])).append("\n");
        }
        sb.append("Valor óptimo = ").append(String.format("%.2f", valorOptimo));
        return sb.toString();
    }
}