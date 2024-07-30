package productos;

import com.fasterxml.jackson.annotation.JsonInclude;

// import java.util.ArrayList;
// import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoIndex extends ProductoBase{
    private String imagen;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoIndex() {}

    public ProductoIndex(Long id, String nombre, Long idCategoria, String imagen) {
        super(id,nombre,idCategoria);
        this.imagen = imagen;
    }
    
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


}
