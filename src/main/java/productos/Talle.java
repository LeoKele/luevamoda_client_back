package productos;

public class Talle {
    private String talle;
    private Double medidaBusto;
    private Double medidaCintura;
    private Double medidaCadera;

    // Constructor
    public Talle(String talle, Double medidaBusto, Double medidaCintura, Double medidaCadera) {
        this.talle = talle;
        this.medidaBusto = medidaBusto;
        this.medidaCintura = medidaCintura;
        this.medidaCadera = medidaCadera;
    }

    public Talle(){};
    // Getters and Setters
    public String getTalle() {
        return talle;
    }

    public void setTalle(String talle) {
        this.talle = talle;
    }

    public Double getMedidaBusto() {
        return medidaBusto;
    }

    public void setMedidaBusto(Double medidaBusto) {
        this.medidaBusto = medidaBusto;
    }

    public Double getMedidaCintura() {
        return medidaCintura;
    }

    public void setMedidaCintura(Double medidaCintura) {
        this.medidaCintura = medidaCintura;
    }

    public Double getMedidaCadera() {
        return medidaCadera;
    }

    public void setMedidaCadera(Double medidaCadera) {
        this.medidaCadera = medidaCadera;
    }
}
