package pdaw.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "evaluacion")
public class Evaluacion implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "fechahora")
    private LocalDateTime fehcahora;
    
    @Column(name = "calificación")
    private int calificación;
	
	@Column(name="comentario")
	private String comentario;
	@ManyToOne
	@JoinColumn(name="idvehiculo")
	private Vehiculo vehiculo;
	@ManyToOne
	@JoinColumn(name="idcliente")
	private Cliente cliente;
	
	
	@OneToOne
	@JoinColumn(name = "idcita")
	private CitasDePrueba citadeprueba;
	
	public Long getId() {
		return id;
	}
	
	public CitasDePrueba getCitadeprueba() {
		return citadeprueba;
	}

	public void setCitadeprueba(CitasDePrueba citadeprueba) {
		this.citadeprueba = citadeprueba;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getFehcahora() {
		return fehcahora;
	}
	public void setFehcahora(LocalDateTime fehcahora) {
		this.fehcahora = fehcahora;
	}
	
	public int getCalificación() {
		return calificación;
	}
	public void setCalificación(int calificación) {
		this.calificación = calificación;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Vehiculo getVehiculo() {
		return vehiculo;
	}
	public void setVehiculo(Vehiculo vehiculo) {
		this.vehiculo = vehiculo;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	public Evaluacion(Long id, LocalDateTime fehcahora, int calificación, String comentario, Vehiculo vehiculo,
			CitasDePrueba citadeprueba, Cliente cliente) {
		super();
		this.id = id;
		this.fehcahora = fehcahora;
		this.calificación = calificación;
		this.comentario = comentario;
		this.vehiculo = vehiculo;
		this.citadeprueba = citadeprueba;
		this.cliente = cliente;
	}

	@Override
	public String toString() {
		return "Evaluacion [id=" + id + ", fehcahora=" + fehcahora + ", calificación=" + calificación + ", comentario="
				+ comentario + ", vehiculo=" + vehiculo + ", cliente=" + cliente + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(calificación, cliente, comentario, fehcahora, id, vehiculo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Evaluacion other = (Evaluacion) obj;
		return calificación == other.calificación && Objects.equals(cliente, other.cliente)
				&& Objects.equals(comentario, other.comentario) && Objects.equals(fehcahora, other.fehcahora)
				&& Objects.equals(id, other.id) && Objects.equals(vehiculo, other.vehiculo);
	}
	public Evaluacion() {
		super();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
 
}