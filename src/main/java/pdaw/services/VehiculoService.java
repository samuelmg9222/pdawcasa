package pdaw.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pdaw.modelo.TipoVehiculo;
import pdaw.modelo.Vehiculo;
import pdaw.repos.VehiculoRepos;

@Service
public class VehiculoService {
@Autowired
VehiculoRepos vehiculorepos;
public void eliminarVehiculo(Long id) {
    vehiculorepos.deleteById(id);
}
	public List<Vehiculo> findAll(){
		return vehiculorepos.findAll();
	}
	public void insertar(Vehiculo vehiculo) {
		vehiculorepos.saveAndFlush(vehiculo);
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
	  public List<TipoVehiculo> obtenertipos() {
		    return Arrays.asList(TipoVehiculo.values());
		}
	  public List<String> obtenerMarcasUnicas() {
	
		    List<Vehiculo> vehiculos = vehiculorepos.findAll();
		    
		    
		    return vehiculos.stream()
		            .map(Vehiculo::getMarca)  
		            .distinct()  
		            .collect(Collectors.toList());
		}
}
