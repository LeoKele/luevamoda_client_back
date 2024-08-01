package productos;
import java.time.LocalDate;

public class Pedido {
    private Long idPedido;
    private Long idCliente;
    private String nombreCliente;
    private LocalDate fechaRecibido;
    private LocalDate fechaFinalizado;
    private String estado;
    private String descripcion;


    public Pedido(){};

    //Metodo constructor con todos los atributos de Pedido
    public Pedido(Long idPedido, Long idCliente, String nombreCliente, LocalDate fechaRecibido, LocalDate fechaFinalizado, String estado, String descripcion){
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fechaRecibido = fechaRecibido;
        this.fechaFinalizado = fechaFinalizado;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    //get y set de todos los atributos de Pedido
    public Long getIdPedido() {return idPedido;}
    public void setIdPedido(Long idPedido) {this.idPedido = idPedido;}
    
    public Long getIdCliente(){return idCliente;}
    public void setIdCliente(Long idCliente) {this.idCliente = idCliente;}

    public String getNombreCliente(){return nombreCliente;}
    public void setNombreCliente(String nombreCliente) {this.nombreCliente = nombreCliente;}

    public LocalDate getFechaRecibido(){return fechaRecibido;}
    public void setFechaRecibido(LocalDate fechaRecibido) {this.fechaRecibido = fechaRecibido;}

    public LocalDate getFechaFinalizado(){return fechaFinalizado;}
    public void setFechaFinalizado(LocalDate fechaFinalizado) {this.fechaFinalizado = fechaFinalizado;}

    public String getEstado(){return estado;}
    public void setEstado(String estado) {this.estado = estado;}

    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    
    

}
