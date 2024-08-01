package productos;

import com.fasterxml.jackson.annotation.JsonProperty;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoAdmin extends ProductoBase{
    private int listado;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoAdmin() {}

    public ProductoAdmin(Long id, String cliente, String nombre, Long idCategoria, Double precioMoldeBase, Double precioMoldeDigital, Double precioMoldeCartulina, Long cantidadTalles, int listado) {
        super(id, cliente,nombre,idCategoria,precioMoldeBase,precioMoldeDigital,precioMoldeCartulina,cantidadTalles);
        this.listado = listado;
    }

    
    @JsonProperty("listado")
    public int getListado(){
        return listado;
    }
    public void setListado(int listado){
        this.listado = listado;
    }


}
