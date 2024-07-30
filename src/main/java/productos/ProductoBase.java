package productos;


public class ProductoBase {
    private Long id;
    private String cliente;
    private String nombre;
    private Long idCategoria;
    private Long medidaBusto;
    private Long medidaCintura;
    private Long medidaCadera;
    private Double precioMoldeBase;
    private Double precioMoldeDigital;
    private Double precioMoldeCartulina;
    private Long cantidadTalles;



    public ProductoBase(Long id, String cliente, String nombre, Long idCategoria, Long medidaBusto, Long medidaCintura, Long medidaCadera, Double precioMoldeBase, Double precioMoldeDigital, Double precioMoldeCartulina, Long cantidadTalles){
        this.id = id;
        this.cliente = cliente;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
        this.medidaBusto = medidaBusto;
        this.medidaCintura = medidaCintura;
        this.medidaCadera = medidaCadera;
        this.precioMoldeBase = precioMoldeBase;
        this.precioMoldeDigital = precioMoldeDigital;
        this.precioMoldeCartulina = precioMoldeCartulina;
        this.cantidadTalles = cantidadTalles;

    }

    public ProductoBase(long id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    //constructor para el JSON index
    public ProductoBase(Long id, String nombre,Long idCategoria ){
        this.id = id;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
    }

    //constructor para el JSON detalle
    public ProductoBase(Long id, String nombre,Long medidaBusto, Long medidaCintura, Long medidaCadera, Long cantidadTalles ){
        this.id = id;
        this.nombre = nombre;
        this.medidaBusto = medidaBusto;
        this.medidaCintura = medidaCintura;
        this.medidaCadera = medidaCadera;
        this.cantidadTalles = cantidadTalles;
    }

    //Constructor vacio necesario para deserializacion de JSON
    public ProductoBase() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getMedidaBusto() {
        return medidaBusto;
    }
    public void setMedidaBusto(Long medidaBusto) {
        this.medidaBusto = medidaBusto;
    }

    public Long getMedidaCintura() {
        return medidaCintura;
    }
    public void setMedidaCintura(Long medidaCintura) {
        this.medidaCintura = medidaCintura;
    }

    public Long getMedidaCadera() {
        return medidaCadera;
    }
    public void setMedidaCadera(Long medidaCadera) {
        this.medidaCadera = medidaCadera;
    }

    public Double getPrecioMoldeBase() {
        return precioMoldeBase;
    }

    public void setPrecioMoldeBase(Double precioMoldeBase) {
        this.precioMoldeBase = precioMoldeBase;
    }

    public Double getPrecioMoldeDigital() {
        return precioMoldeDigital;
    }

    public void setPrecioMoldeDigital(Double precioMoldeDigital) {
        this.precioMoldeDigital = precioMoldeDigital;
    }

    public Double getPrecioMoldeCartulina() {
        return precioMoldeCartulina;
    }

    public void setPrecioMoldeCartulina(Double precioMoldeCartulina) {
        this.precioMoldeCartulina = precioMoldeCartulina;
    }

    public Long getCantidadTalles(){
        return cantidadTalles;
    }
    public void setCantidadTalles(Long cantidadTalles){
        this.cantidadTalles = cantidadTalles;
    }
    

}
