package pdaw.modelo;

import java.io.Serializable;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "vehiculos")
public class Vehiculo implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "marca")
    private String marca;
    
    @Column(name = "modelo")
    private String modelo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoVehiculo tipo;
    
    @Column(name = "precio")
    private Double precio;
    
    @Column(name = "añofabricacion")
    private int añofabricacion;
    
    @Column(name = "kilometraje")
    private int kilometraje;
    @Column(name = "foto")  
    private String foto;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "disponible")
    private boolean disponible = true;
    @OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="idcitadeprueba")
	private List<CitasDePrueba> citasdeprueba=new LinkedList<CitasDePrueba>();
    @OneToMany(cascade=CascadeType.ALL)
  	@JoinColumn(name="idevaluacion")
  	private List<Evaluacion> evaluaciones=new LinkedList<Evaluacion>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	  public String getFoto() {
	        return foto;
	    }

	    public void setFoto(String foto) {
	        this.foto = foto;
	    }
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public TipoVehiculo getTipo() {
		return tipo;
	}
	public void setTipo(TipoVehiculo tipo) {
		this.tipo = tipo;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public int getAñofabricacion() {
		return añofabricacion;
	}
	public void setAñofabricacion(int añofabricacion) {
		this.añofabricacion = añofabricacion;
	}
	public int getKilometraje() {
		return kilometraje;
	}
	public void setKilometraje(int kilometraje) {
		this.kilometraje = kilometraje;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public boolean isDisponible() {
		return disponible;
	}
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
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
	@Override
	public String toString() {
		return "Vehiculo [id=" + id + ", marca=" + marca + ", modelo=" + modelo + ", tipo=" + tipo + ", precio="
				+ precio + ", añofabricacion=" + añofabricacion + ", kilometraje=" + kilometraje + ", color=" + color
				+ ", descripcion=" + descripcion + ", disponible=" + disponible + ", citasdeprueba=" + citasdeprueba
				+ ", evaluaciones=" + evaluaciones + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(añofabricacion, citasdeprueba, color, descripcion, disponible, evaluaciones, id,
				kilometraje, marca, modelo, precio, tipo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehiculo other = (Vehiculo) obj;
		return añofabricacion == other.añofabricacion && Objects.equals(citasdeprueba, other.citasdeprueba)
				&& Objects.equals(color, other.color) && Objects.equals(descripcion, other.descripcion)
				&& disponible == other.disponible && Objects.equals(evaluaciones, other.evaluaciones)
				&& Objects.equals(id, other.id) && kilometraje == other.kilometraje
				&& Objects.equals(marca, other.marca) && Objects.equals(modelo, other.modelo)
				&& Objects.equals(precio, other.precio) && tipo == other.tipo;
	}
	public Vehiculo(Long id, String marca, String modelo, TipoVehiculo tipo, Double precio, int añofabricacion,
			int kilometraje, String color, String descripcion, boolean disponible, List<CitasDePrueba> citasdeprueba,
			List<Evaluacion> evaluaciones) {
		super();
		this.id = id;
		this.marca = marca;
		this.modelo = modelo;
		this.tipo = tipo;
		this.precio = precio;
		this.añofabricacion = añofabricacion;
		this.kilometraje = kilometraje;
		this.color = color;
		this.descripcion = descripcion;
		this.disponible = disponible;
		this.citasdeprueba = citasdeprueba;
		this.evaluaciones = evaluaciones;
	}
	public Vehiculo() {
		super();
	}
    
    
}

