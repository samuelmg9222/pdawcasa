package pdaw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pdaw.modelo.Vehiculo;
import pdaw.repos.VehiculoRepos;

@Service
public class VehiculoService {
@Autowired
VehiculoRepos vehiculorepos;
	
	public List<Vehiculo> findAll(){
		return vehiculorepos.findAll();
	}
	  public Page<Vehiculo> obtenerVehiculosPaginados(Pageable pageable) {
	        return vehiculorepos.findAll(pageable);  
	    }
	  public Vehiculo findById(Long id) {
		    return vehiculorepos.findById(id).orElse(null);
		}
	  
	  public void insertarVehiculo(Vehiculo v) {
		  vehiculorepos.saveAndFlush(v);
	  }
}
