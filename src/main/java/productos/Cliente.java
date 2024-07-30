package productos;

public class Cliente {
    private Long id;
    private String nombre;
    private String telefono;
    private String mail;


    public Cliente(){};

    public Cliente(Long id, String nombre, String telefono, String mail){
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.mail = mail;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}

    public String getTelefono(){return telefono;}
    public void setTelefono(String telefono){this.telefono = telefono;}

    public String getMail(){return mail;}
    public void setMail(String mail){this.mail = mail;}
}
