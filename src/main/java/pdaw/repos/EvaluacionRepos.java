package pdaw.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pdaw.modelo.CitasDePrueba;
import pdaw.modelo.Cliente;
import pdaw.modelo.Evaluacion;

public interface EvaluacionRepos extends JpaRepository <Evaluacion,Long>{
	 List<Evaluacion> findByCliente(Cliente cliente);
}
