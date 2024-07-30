package productos;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

// import java.util.ArrayList;
// import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDetalle extends ProductoBase{
    private String imagenes;  // Campo para las URLs de las imágenes



    //Constructor vacio necesario para deserializacion de JSON
    public ProductoDetalle() {}

    public ProductoDetalle(Long id, String nombre, Long medidaBusto, Long medidaCintura, Long medidaCadera, Long cantidadTalles, String imagen) {
        super(id,nombre,medidaBusto,medidaCintura,medidaCadera,cantidadTalles);
        this.imagenes = imagen;
    }
    
    @JsonProperty("imagenes")
    public String getImagen() {
        return imagenes;
    }

    public void setImagen(String imagenes) {
        this.imagenes = imagenes;
    }


}
