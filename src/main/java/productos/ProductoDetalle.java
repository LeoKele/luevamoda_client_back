package productos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDetalle extends ProductoBase {
    private Long idCategoria;
    private String listado;
    private String imagenes;
    private Double precio;

    public ProductoDetalle() {}

    public ProductoDetalle(Long id, String nombre, Long idCategoria, String listado, String imagenes, Double precio) {
        super(id, nombre);
        this.idCategoria = idCategoria;
        this.listado = listado;
        this.imagenes = imagenes;
        this.precio = precio;
    }

    @JsonProperty("id_categoria")
    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    @JsonProperty("listado")
    public String getListado() {
        return listado;
    }

    public void setListado(String listado) {
        this.listado = listado;
    }

    @JsonProperty("imagenes")
    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }

    @JsonProperty("precio")
    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
