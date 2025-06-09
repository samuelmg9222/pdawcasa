package pdaw.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@Table(name = "citasdeprueba")
public class CitasDePrueba implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "fechahora")
    private LocalDateTime fechahora;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado tipo; // Ahora referenciamos el enum Estado

    @ManyToOne
    @JoinColumn(name="idvehiculo")
    private Vehiculo vehiculo;
    
    @ManyToOne
    @JoinColumn(name="idcliente")
    private Cliente cliente;

    @OneToOne(mappedBy = "citadeprueba", optional = true)  
    private Evaluacion evaluacion;
    
    
    public Long getId() {
        return id;
    }
    public String getFechahoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.fechahora.format(formatter);
    }
    public Evaluacion getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechahora() {
        return fechahora;
    }

    public void setFechahora(LocalDateTime fechahora) {
        this.fechahora = fechahora;
    }

    public Estado getTipo() {
        return tipo;
    }

    public void setTipo(Estado tipo) {
        this.tipo = tipo;
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

    // Métodos de comparación
    @Override
    public String toString() {
        return "CitasDePrueba [id=" + id + ", fechahora=" + fechahora + ", tipo=" + tipo + ", vehiculo=" + vehiculo
                + ", cliente=" + cliente + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(cliente, fechahora, id, tipo, vehiculo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CitasDePrueba other = (CitasDePrueba) obj;
        return Objects.equals(cliente, other.cliente) && Objects.equals(fechahora, other.fechahora)
                && Objects.equals(id, other.id) && tipo == other.tipo && Objects.equals(vehiculo, other.vehiculo);
    }

    // Constructores
    public CitasDePrueba(Long id, LocalDateTime fechahora, Estado tipo, Vehiculo vehiculo, Cliente cliente) {
        this.id = id;
        this.fechahora = fechahora;
        this.tipo = tipo;
        this.vehiculo = vehiculo;
        this.cliente = cliente;
    }

    public CitasDePrueba() {
        // Constructor por defecto
    }
}
