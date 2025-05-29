package pdaw.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "dechareg")
    private LocalDate fehcahora;
    
    
    @Column(name = "dni")
    private String dni;
    
	@ManyToOne
	@JoinColumn
	private Vehiculo vehiculo;
    
	 @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
	private List<CitasDePrueba> citasdeprueba=new LinkedList<CitasDePrueba>();
    
	@OneToMany(mappedBy = "cliente", cascade=CascadeType.ALL)
	private List<Evaluacion> evaluaciones=new LinkedList<Evaluacion>();
    @OneToOne
    @JoinColumn(name = "idusuario")  
    private Usuario usuario;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getFehcahora() {
		return fehcahora;
	}
	public void setFehcahora(LocalDate fehcahora) {
		this.fehcahora = fehcahora;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public Vehiculo getVehiculo() {
		return vehiculo;
	}
	public void setVehiculo(Vehiculo vehiculo) {
		this.vehiculo = vehiculo;
	}
	public List<CitasDePrueba> getCitasdeprueba() {
		return citasdeprueba;
	}
	public void setCitasdeprueba(List<CitasDePrueba> citasdeprueba) {
		this.citasdeprueba = citasdeprueba;
	}
	public List<Evaluacion> getEvaluaciones() {
		return evaluaciones;
	}
	public void setEvaluaciones(List<Evaluacion> evaluaciones) {
		this.evaluaciones = evaluaciones;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Cliente(Long id, LocalDate fehcahora, String dni, Vehiculo vehiculo, List<CitasDePrueba> citasdeprueba,
			List<Evaluacion> evaluaciones, Usuario usuario) {
		super();
		this.id = id;
		this.fehcahora = fehcahora;
		this.dni = dni;
		this.vehiculo = vehiculo;
		this.citasdeprueba = citasdeprueba;
		this.evaluaciones = evaluaciones;
		this.usuario = usuario;
	}
	public Cliente() {
		super();
	}
	@Override
	public int hashCode() {
		return Objects.hash(citasdeprueba, dni, evaluaciones, fehcahora, id, usuario, vehiculo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(citasdeprueba, other.citasdeprueba) && Objects.equals(dni, other.dni)
				&& Objects.equals(evaluaciones, other.evaluaciones) && Objects.equals(fehcahora, other.fehcahora)
				&& Objects.equals(id, other.id) && Objects.equals(usuario, other.usuario)
				&& Objects.equals(vehiculo, other.vehiculo);
	}
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", fehcahora=" + fehcahora + ", dni=" + dni + ", vehiculo=" + vehiculo
				+ ", citasdeprueba=" + citasdeprueba + ", evaluaciones=" + evaluaciones + ", usuario=" + usuario + "]";
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}