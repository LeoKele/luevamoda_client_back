package productos;

public class TalleConId extends Talle {
    private Long id; // ID del talle, necesario para operaciones de actualización y eliminación
    private Long idProducto; // ID del producto asociado, necesario para insertar o actualizar

    // Constructor con todos los atributos
    public TalleConId(Long id, Long idProducto, String talle, Double medidaBusto, Double medidaCintura, Double medidaCadera) {
        super(talle, medidaBusto, medidaCintura, medidaCadera); // Llama al constructor de la clase base
        this.id = id;
        this.idProducto = idProducto;
    }

    // Constructor sin ID, útil para inserciones
    public TalleConId(Long idProducto, String talle, Double medidaBusto, Double medidaCintura, Double medidaCadera) {
        this(null, idProducto, talle, medidaBusto, medidaCintura, medidaCadera);
    }

    public TalleConId(){};
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
}
