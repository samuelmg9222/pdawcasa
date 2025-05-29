package pdaw.controller;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pdaw.modelo.*;
import pdaw.services.*;




@Controller
public class MainController {
	
	@Autowired
	VehiculoService servvehiculo;
	
	
	
	
	@GetMapping("/")
	public String mostrarPagina(Model model, Authentication authentication) {
	  List<Vehiculo>todos=servvehiculo.findAll();
	  List<Vehiculo>destacados=new ArrayList<Vehiculo>();
	  for (int i = 0; i <= 5 && i < todos.size(); i++) {
		    destacados.add(todos.get(i));
		}
	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String rol = auth.getAuthorities().stream()
                       .map(GrantedAuthority::getAuthority)
                       .findFirst()
                       .orElse(null);
    System.out.println(rol);  
    model.addAttribute("rol", rol);
	    model.addAttribute("vehiculos", destacados);
	    model.addAttribute("authentication", authentication);
	    return "index";
	}
		   
	@GetMapping("/vertodos")
    public String vertodos(@RequestParam(defaultValue = "0") int page,
            @RequestParam Map<String, String> allParams,
                           @RequestParam(required = false) List<String> marca,
                           @RequestParam(required = false) List<TipoVehiculo> tipo,
                           @RequestParam(required = false) Integer kilometrajeMin,
                           @RequestParam(required = false) Integer kilometrajeMax,
                           @RequestParam(required = false) Double precioMin,
                           @RequestParam(required = false) Double precioMax,
                           @RequestParam(required = false) Integer anioMax,
                           @RequestParam(required = false) Integer anioMin,
                           Model model) {

       
        List<Vehiculo> vehiculos = servvehiculo.findAll();

        List<TipoVehiculo> tipos = servvehiculo.obtenertipos();
        model.addAttribute("tipos", tipos);  
        List<String> marcas = servvehiculo.obtenerMarcasUnicas();
        model.addAttribute("marcas", marcas);  // Pasar las marcas al modelo

        // Filtrar los vehículos por marca y otros parámetros
        if (marca != null && !marca.isEmpty()) {
            vehiculos = vehiculos.stream()
                    .filter(v -> marca.contains(v.getMarca()))  // Filtrar por marca
                    .collect(Collectors.toList());
        }
        if (tipo != null && !tipo.isEmpty()) {
            vehiculos = vehiculos.stream()
                    .filter(v -> tipo.contains(v.getTipo()))  
                    .collect(Collectors.toList());
        }

        if (kilometrajeMin != null && kilometrajeMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getKilometraje() >= kilometrajeMin && v.getKilometraje() <= kilometrajeMax)
                    .collect(Collectors.toList());
        } else if (kilometrajeMin != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getKilometraje() >= kilometrajeMin)
                    .collect(Collectors.toList());
        } else if (kilometrajeMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getKilometraje() <= kilometrajeMax)
                    .collect(Collectors.toList());
        }

        if (precioMin != null && precioMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getPrecio() >= precioMin && v.getPrecio() <= precioMax)
                    .collect(Collectors.toList());
        } else if (precioMin != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getPrecio() >= precioMin)
                    .collect(Collectors.toList());
        } else if (precioMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getPrecio() <= precioMax)
                    .collect(Collectors.toList());
        }

        if (anioMin != null && anioMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getAñofabricacion() >= anioMin && v.getAñofabricacion() <= anioMax)
                    .collect(Collectors.toList());
        } else if (anioMin != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getAñofabricacion() >= anioMin)
                    .collect(Collectors.toList());
        } else if (anioMax != null) {
            vehiculos = vehiculos.stream()
                    .filter(v -> v.getAñofabricacion() <= anioMax)
                    .collect(Collectors.toList());
        }

        
        int pageSize = 6;
        int totalVehicles = vehiculos.size();
        int start = Math.min(page * pageSize, totalVehicles);
        int end = Math.min((page + 1) * pageSize, totalVehicles);
        List<Vehiculo> paginatedVehiculos = vehiculos.subList(start, end);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String rol = auth.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority)
                         .findFirst()
                         .orElse(null);
      
        model.addAttribute("rol", rol);
        model.addAttribute("vehiculos", paginatedVehiculos);
        model.addAttribute("totalPages", (totalVehicles + pageSize - 1) / pageSize); 
        model.addAttribute("currentPage", page);
        model.addAttribute("marca", marca);
	    model.addAttribute("tipo", tipo);
	    model.addAttribute("kilometrajeMin", kilometrajeMin);
	    model.addAttribute("kilometrajeMax", kilometrajeMax);
	    model.addAttribute("precioMin", precioMin);
	    model.addAttribute("precioMax", precioMax);
	    model.addAttribute("anioMin", anioMin);
	    model.addAttribute("anioMax", anioMax);
        return "vertodos";
    }

		    @GetMapping("/login")
		    public String mostrarLogin(@RequestParam(value = "error", required = false) String error, 
		                               Model model, 
		                               Authentication authentication) {
		        if (authentication != null && authentication.getAuthorities().stream()
		                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
		            return "/admin";
		        }
		        if (authentication != null && authentication.getAuthorities().stream()
		                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PERSONAL"))) {
		            return "/personal";
		        }
		        if (authentication != null && authentication.getAuthorities().stream()
		                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENTE"))) {
		        	return "redirect:/vertodos";
		        }
		        
		    
		        if (error != null) {
		            model.addAttribute("error", "Credenciales incorrectas. Intenta de nuevo.");
		        }
		        

		        return "login";
		    }
		    @PostMapping("/logout")
		    public String logout() {
		       
		        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        if (auth != null) {
		            SecurityContextHolder.clearContext();
		        }
		        
		    
		        return "redirect:/";
		    }
		   
		    
		    @GetMapping("/detalles/{id}")
		    public String verDetalles(@PathVariable Long id, Model model,Authentication authentication) {
		        Vehiculo vehiculo = servvehiculo.findById(id);

		        if (vehiculo == null) {
		            return "redirect:/catalogo";
		        }

		       
		        List<Vehiculo> todos = servvehiculo.findAll();
		        todos.remove(vehiculo);
		        
		        TipoVehiculo tipoActual = vehiculo.getTipo(); 
		        List<Vehiculo> mismosTipo = new ArrayList<>();
		        for (Vehiculo v : todos) {
		            if (v.getTipo() == tipoActual && !v.getId().equals(id)) {
		                mismosTipo.add(v);
		            }
		        }

		        
		        Collections.shuffle(mismosTipo);
		        List<Vehiculo> destacados = new ArrayList<>();
		        for (int i = 0; i < 6 && i < mismosTipo.size(); i++) {
		            destacados.add(mismosTipo.get(i));
		        }
		        model.addAttribute("authentication", authentication);
		        model.addAttribute("vehiculo", vehiculo);
		        model.addAttribute("vehiculos", destacados);
		        return "detalles";
		    }
		
		 

}