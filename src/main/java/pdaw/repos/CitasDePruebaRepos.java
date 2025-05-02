package pdaw.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pdaw.modelo.CitasDePrueba;
import pdaw.modelo.Cliente;
import pdaw.modelo.Estado;

public interface CitasDePruebaRepos extends JpaRepository <CitasDePrueba,Long>{
	List<CitasDePrueba> findByTipo(Estado tipo);
    List<CitasDePrueba> findByCliente(Cliente cliente);
}
