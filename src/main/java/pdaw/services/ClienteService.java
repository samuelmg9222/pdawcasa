package pdaw.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdaw.modelo.Cliente;
import pdaw.modelo.Usuario;
import pdaw.repos.ClienteRepos;
import pdaw.repos.UsuarioRepos;


@Service
public class ClienteService {

	@Autowired
	ClienteRepos clienterepos;
	@Autowired
	UsuarioService servusuario;
	
	public int validarCliente(Cliente cliente, Usuario usuario) {
	    if (usuario.getNombre() == null || usuario.getNombre().length() < 3 || usuario.getNombre().length() > 20) {
	        return -2; 
	    }
	    if (!usuario.getNombre().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
	        return -3; 
	    }
	    if (usuario.getFechanac() == null) {
	        return -4; 
	    }
	    
	    Date fechaNacimiento = usuario.getFechanac();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(fechaNacimiento);
	    
	    int anioNacimiento = calendar.get(Calendar.YEAR);
	    int mesNacimiento = calendar.get(Calendar.MONTH);
	    int diaNacimiento = calendar.get(Calendar.DAY_OF_MONTH);

	    Calendar calendarHoy = Calendar.getInstance();
	    int anioHoy = calendarHoy.get(Calendar.YEAR);
	    int mesHoy = calendarHoy.get(Calendar.MONTH);
	    int diaHoy = calendarHoy.get(Calendar.DAY_OF_MONTH);
	    
	    int edad = anioHoy - anioNacimiento;
	    if (mesHoy < mesNacimiento || (mesHoy == mesNacimiento && diaHoy < diaNacimiento)) {
	        edad--;
	    }
	    if (edad < 18) {
	        return -5;
	    }
	    int dniValidacion = validarDNI(cliente.getDni());
	    if (dniValidacion == -1) {
	        return -6;  
	    }

	 
	    if (usuario.getCorreo()== null || !usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	        return -7; 
	    }
	
	    if(servusuario.exiteUser(usuario.getUser())==0) {
	    	return -8;
	    }
	   
	    if (servusuario.existeNEmail(usuario.getCorreo()) == 1) {
	        
	        return -9;  
	    } 
	    if (usuario.getApellidos() == null || usuario.getApellidos().isEmpty()) {
	        return -11; 
	    }
	    if (usuario.getApellidos().length() < 3 || usuario.getApellidos().length() > 50) {
	        return -12; 
	    }
	    if (!usuario.getApellidos().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
	        return -13;
	    }

	    if (usuario.getContraseña() == null || usuario.getContraseña().length() < 8) {
	        return -10; 
	    }
	    if (!usuario.getContraseña().matches(".*[A-Z].*")) {
	        return -14;
	    }
	    if (!usuario.getContraseña().matches(".*[a-z].*")) {
	        return -15; 
	    }
	    if (!usuario.getContraseña().matches(".*\\d.*")) {
	        return -16;
	    }
	    if (!usuario.getContraseña().matches(".*[!@#$%^&*(),_.?\":{}|<>].*")) {
	        return -17;
	    }
	    if (dniValidacion == -2) {
	        return -18;  
	    }
	    if (dniValidacion == -3) {
	        return -19;  
	    }
	    if(existeNif(cliente.getDni().trim())==1) {
	    	return 21;
	    }
	    if(!usuario.getUser().matches("^[a-zA-Z0-9_]+$")) {
	    	return 20;
	    }
	  
	    return 0; 
	}



	public int existeNif(String nif) {
	    List<Cliente> todos = clienterepos.findAll();

	    for (Cliente c : todos) {
	        if (c.getDni() != null && nif != null && c.getDni().equalsIgnoreCase(nif)) {
	            return 0; 
	        }
	    }
	    return 1; 
	}
	public void insertarcliente(Cliente cliente) {
		
	clienterepos.saveAndFlush(cliente);
		
	}
	  public Cliente findById(Long id) {
	        return clienterepos.findById(id)
	                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
	    }
	
	public int validarDNI(String nif) {
	    
	    if (nif == null || nif.length() != 9) {
	        return -1; 
	    }

	   
	    String numero = nif.substring(0, 8);
	    char letra = Character.toUpperCase(nif.charAt(8));

	  
	    if (!numero.matches("\\d{8}")) {
	        return -2;
	    }

	    
	    int num = Integer.parseInt(numero);

	    
	    char letraCalculada = LETRAS_DNI[num % 23];

	    
	    if (letra != letraCalculada) {
	        return -3; 
	    }

	    return 1;
	}

	
	private static final char[] LETRAS_DNI = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};

}