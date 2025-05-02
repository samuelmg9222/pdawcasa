package pdaw.modelo;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user")
    private String user;
    @Column(name = "contraseña")
    private String contraseña;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "correo")
    private String correo;
    @Column(name = "fechanac")
    private Date fechanac;
    @Column(name = "fechaCodigoRecuperacion")
    private LocalDateTime fechaCodigoRecuperacion;
    @Column
    private String codigoRecuperacion;
    @OneToOne(mappedBy = "usuario", optional = true)  
    private Cliente cliente;

	public Long getId() {
		return id;
	}

	public LocalDateTime getFechaCodigoRecuperacion() {
		return fechaCodigoRecuperacion;
	}

	public void setFechaCodigoRecuperacion(LocalDateTime fechaCodigoRecuperacion) {
		this.fechaCodigoRecuperacion = fechaCodigoRecuperacion;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigoRecuperacion() {
		return codigoRecuperacion;
	}

	public void setCodigoRecuperacion(String codigoRecuperacion) {
		this.codigoRecuperacion = codigoRecuperacion;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Date getFechanac() {
		return fechanac;
	}

	public void setFechanac(Date fechanac) {
		this.fechanac = fechanac;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario(Long id, String user, String contraseña, String nombre, String apellidos, String correo, Date fechanac,
			Cliente cliente) {
		super();
		this.id = id;
		this.user = user;
		this.contraseña = contraseña;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.correo = correo;
		this.fechanac = fechanac;
		this.cliente = cliente;
	}

	public Usuario() {
		super();
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", user=" + user + ", contraseña=" + contraseña + ", nombre=" + nombre
				+ ", apellidos=" + apellidos + ", correo=" + correo + ", Date=" + fechanac + ", cliente=" + cliente + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechanac, apellidos, cliente, contraseña, correo, id, nombre, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(fechanac, other.fechanac) && Objects.equals(apellidos, other.apellidos)
				&& Objects.equals(cliente, other.cliente) && Objects.equals(contraseña, other.contraseña)
				&& Objects.equals(correo, other.correo) && Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(user, other.user);
	}

	
    
    
    
    
}
