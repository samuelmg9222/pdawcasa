package pdaw.controller;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
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
	    model.addAttribute("vehiculos", destacados);
	    model.addAttribute("authentication", authentication);
	    return "index";
	}
		   
	@GetMapping("/vertodos")
	public String vertodos(@RequestParam(defaultValue = "0") int page, Model model) {
	    Page<Vehiculo> vehiculosPage = servvehiculo.obtenerVehiculosPaginados(PageRequest.of(page, 6)); 
	    model.addAttribute("vehiculos", vehiculosPage.getContent());
	    model.addAttribute("totalPages", vehiculosPage.getTotalPages());
	    model.addAttribute("currentPage", page);
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