package productos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Venta {
    private Long id;
    private Long idCliente;
    private String nombreCliente;
    private Long idProducto;
    private String nombreProducto;
    private LocalDate fechaVenta;
    private Long cantidad;
    private Double precioUnitario;
    private Double total;


    public Venta(){};

    public Venta(Long id, Long idCliente, String nombreCliente, Long idProducto, String nombreProducto,LocalDate fechaVenta, Long cantidad, Double precioUnitario,Double total){
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.fechaVenta = fechaVenta;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    //getter y setter
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public Long getIdCliente(){
        return idCliente;
    }
    public void setIdCliente(Long idCliente){
        this.idCliente = idCliente;
    }

    public String getNombreCliente(){
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente){
        this.nombreCliente = nombreCliente;
    }

    public Long getIdProducto(){
        return idProducto;
    }
    public void setIdProducto(Long idProducto){
        this.idProducto = idProducto;
    }

    public String getNombreProducto(){
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto){
        this.nombreProducto = nombreProducto;
    }

    public LocalDate getFechaVenta(){
        return fechaVenta;
    }
    public void setFechaVenta(LocalDate fechaVenta){
        this.fechaVenta = fechaVenta;
    }

    public Long getCantidad(){
        return cantidad;
    }
    public void setCantidad(Long cantidad){
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario(){
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario){
        this.precioUnitario = precioUnitario;
    }
    
    public Double getTotal(){
        return total;
    }

    public void setTotal(Double total){
        this.total = total;
    }



}
