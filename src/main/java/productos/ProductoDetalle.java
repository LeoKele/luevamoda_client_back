package productos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDetalle extends ProductoBase {
    private Long idCategoria;
    private String listado;
    private List<Talle> talles;
    private String imagenes;

    public ProductoDetalle() {}

    public ProductoDetalle(Long id, String nombre, Long idCategoria, String listado, Long cantidadTalles, List<Talle> talles, String imagenes) {
        super(id, nombre, cantidadTalles);
        this.idCategoria = idCategoria;
        this.listado = listado;
        this.talles = talles;
        this.imagenes = imagenes;
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

    @JsonProperty("talles")
    public List<Talle> getTalles() {
        return talles;
    }

    public void setTalles(List<Talle> talles) {
        this.talles = talles;
    }

    @JsonProperty("imagenes")
    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }
}
