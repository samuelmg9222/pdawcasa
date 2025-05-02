package pdaw.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdaw.modelo.CitasDePrueba;
import pdaw.modelo.Cliente;
import pdaw.modelo.Evaluacion;
import pdaw.repos.EvaluacionRepos;

@Service
public class EvaluacionService {
@Autowired
EvaluacionRepos evaluacionrepos;

	public void save(Evaluacion ev) {
		evaluacionrepos.saveAndFlush(ev);
	}
	  public Evaluacion findById(Long id) {
		    
	        return evaluacionrepos.findById(id).orElse(null);
	      
	    }
	  public List<Evaluacion> findByCliente(Cliente cliente) {
	        return evaluacionrepos.findByCliente(cliente);
	    }
}
