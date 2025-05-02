package pdaw.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pdaw.modelo.*;
import pdaw.services.CitaDePruebaService;
import pdaw.services.ClienteService;
import pdaw.services.EvaluacionService;
import pdaw.services.UsuarioService;
import pdaw.services.VehiculoService;



@Controller
public class ClienteController {
	@Autowired
	VehiculoService servvehiculo;
	 @Autowired
	private JavaMailSender mailSender;
	@Autowired
	ClienteService servcliente;
	@Autowired
	UsuarioService servusuario;
	@Autowired
	CitaDePruebaService servcita;
	@Autowired
	EvaluacionService servevaluacion;
	@GetMapping("/registro")
	public String mostrarFormulario(Model model) {
	    model.addAttribute("cliente", new Cliente());
	    model.addAttribute("usuario", new Usuario());
	    return "registro"; 
	}
	@GetMapping("/regexitoso")
	public String regex(Model model) {
	 
	    return "regexitoso"; 
	}
	
	@PostMapping("/registrar-cliente")
	public String crearCliente(@ModelAttribute Cliente cliente, @ModelAttribute Usuario usuario, Model model) {
	    int validacion = servcliente.validarCliente(cliente, usuario);

	    if (validacion < 0) {
	        String mensajeError = switch (validacion) {
	            case -2 -> "El nombre debe tener entre 3 y 20 caracteres.";
	            case -3 -> "El nombre solo puede contener letras y espacios.";
	            case -4 -> "La fecha de nacimiento no puede estar vacía.";
	            case -5 -> "Debes ser mayor de 18 años.";
	            case -6 -> "El DNI introducido no es válido.";
	            case -7 -> "El correo no es válido.";
	            case -8 -> "Ya hay alguien con ese nombre de usuario.";
	            case -9 -> "El correo ya está registrado.";
	            case -11 -> "Los apellidos no pueden estar vacíos.";
	            case -12 -> "Los apellidos deben tener entre 3 y 50 caracteres.";
	            case -13 -> "Los apellidos solo pueden contener letras y espacios.";
	            case -10 -> "La contraseña debe tener al menos 8 caracteres.";
	            case -14 -> "La contraseña debe contener al menos una letra mayúscula.";
	            case -15 -> "La contraseña debe contener al menos una letra minúscula.";
	            case -16 -> "La contraseña debe contener al menos un número.";
	            case -17 -> "La contraseña debe contener al menos un carácter especial.";
	            case -18 -> "El dni no es valido.";
	            case -19 -> "El dni no es valido.";
	            case -20 -> "El dni ya esta registrado.";
	            case -21 -> "El nombre de usuario no es valido.";
	            default -> "Error desconocido.";
	        };

	        model.addAttribute("error", mensajeError);  // Agregar el mensaje de error
	        return "registro";  // Devolver a la página de registro con el error
	    }

	    try {
	        // Convertir los valores a minúsculas antes de guardarlos
	        cliente.setDni(cliente.getDni().toLowerCase());
	        usuario.setNombre(usuario.getNombre().toLowerCase());
	        usuario.setCorreo(usuario.getCorreo().toLowerCase());

	        cliente.setFehcahora(LocalDate.now());
	        servusuario.insertarUser(usuario); // Registrar el usuario

	        cliente.setUsuario(usuario); 
	        servcliente.insertarcliente(cliente);

	        
	        return "regexitoso";  
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Ocurrió un error al registrar el cliente.");
	        return "registro";  // Volver al formulario con el mensaje de error
	    }
	}

	
	
	@PostMapping("/paginarestaurar")
	public String recuperarContrasena(@RequestParam String usernameOrEmail, Model model,HttpServletRequest request) {
	

	  
	    
	    int existe = servusuario.findByUserOrEmail(usernameOrEmail.trim().toLowerCase());
	    
	    
	    if (existe != 1) {
	        model.addAttribute("error", "Usuario o correo electrónico no encontrado.");
	        return "recuperarcontraseña"; 
	    }

	   
	    String codigoRecuperacion = generarCodigoRecuperacion();
	    
	   
	    Usuario usuario = servusuario.findByCorreo(usernameOrEmail);
if(usuario==null) {
	   usuario = servusuario.findByUser(usernameOrEmail);
}
	   
	    enviarCorreo(usuario.getCorreo(), codigoRecuperacion);

	    usuario.setCodigoRecuperacion(codigoRecuperacion);
	    usuario.setFechaCodigoRecuperacion(LocalDateTime.now());
	    servusuario.insertarUser(usuario);
	
	    model.addAttribute("mensaje", "Se ha enviado un código de recuperación a tu correo.");
	    
	  
	    return "paginarestaurar";
	}


	    private String generarCodigoRecuperacion() {
	        Random random = new Random();
	        int codigo = random.nextInt(999999); 
	        return String.format("%06d", codigo);
	    }

	    private void enviarCorreo(String correoDestino, String codigo) {
	        SimpleMailMessage mensaje = new SimpleMailMessage();
	        mensaje.setTo(correoDestino);
	        mensaje.setSubject("Recuperación de contraseña");
	        mensaje.setText("Tu código de recuperación es: " + codigo);
	        Usuario user=servusuario.findByCorreo(correoDestino);
	        user.setCodigoRecuperacion(codigo);
	        servusuario.insertarUser(user);
	        mensaje.setFrom("samuelmg92@educastur.es"); 

	        mailSender.send(mensaje);
	    }

	    @GetMapping("/recuperarcontraseña")
	    public String mostrarFormularioRecuperación(Model model) {
	        return "recuperarcontraseña"; 
	    }
	    @PostMapping("/verificar-codigo")
	    public String verificarCodigo(@RequestParam String codigoRecuperacion,
	                                  @RequestParam String nuevaContrasena,
	                                  Model model,
	                                  HttpServletRequest request) {

	        Usuario usuario = servusuario.findByCodigoRecuperacion(codigoRecuperacion);

	        if (usuario == null) {
	            model.addAttribute("error", "Código de recuperación inválido.");
	            return "paginarestaurar";
	        }

	        LocalDateTime fechaGeneracion = usuario.getFechaCodigoRecuperacion();
	        if (fechaGeneracion == null || Duration.between(fechaGeneracion, LocalDateTime.now()).toMinutes() > 30) {
	            model.addAttribute("error", "El código ha caducado. Por favor, solicita uno nuevo.");
	            return "paginarestaurar";
	        }

	        if (!nuevaContrasena.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$")) {
	            model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, un número y un símbolo (@$!%*?&_).");
	            return "paginarestaurar";
	        }
  usuario.setContraseña(nuevaContrasena);
	        usuario.setCodigoRecuperacion(null);
	        usuario.setFechaCodigoRecuperacion(null);
	        servusuario.insertarUser(usuario);

	        model.addAttribute("mensaje", "Contraseña cambiada exitosamente.");
	        return "paginarestaurar";
	    }


	  
		@GetMapping("/cliente")
		public String mostrarPaginaCleinte(Model model) {
		
		   
		    return "cliente";
		}
	
		@GetMapping("/reservar/{id}")
		public String reserva(@PathVariable Long id, Model model) {
		    Vehiculo vehiculo = servvehiculo.findById(id);

		    if (vehiculo == null) {
		        return "redirect:/vertodos";
		    }

		    
		    model.addAttribute("vehiculo", vehiculo);
		    

		  
		   
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

		 
		    model.addAttribute("vehiculo", vehiculo);
		    model.addAttribute("vehiculos", destacados);
		    return "reservar";
		}
	
	

		

		    @PostMapping("/reservar/{id}")
		    public String completarreserva(@PathVariable Long id, 
		                                   @RequestParam("fechaReserva") String fechaReserva, 
		                                   @RequestParam("horaReserva") String horaReserva, 
		                                   @AuthenticationPrincipal UserDetails userDetails, 
		                                   Model model) {

		        Vehiculo vehiculo = servvehiculo.findById(id);

		        if (vehiculo == null) {
		            return "redirect:/vertodos";
		        }
		        model.addAttribute("vehiculo", vehiculo);
		        if (!vehiculo.isDisponible()) {
		        	model.addAttribute("error", "Este vehiculo ya ha sido reservado.");
		            return "reservar"; 
		        }
		        

		        String username = userDetails.getUsername();
		        CitasDePrueba cita = new CitasDePrueba();
		        Usuario userReserva = servusuario.findByUsername(username);
		        Cliente clientereserva = userReserva.getCliente();

		        String fechaHora = fechaReserva + "T" + horaReserva;
		        LocalDateTime fecha = LocalDateTime.parse(fechaHora);

		        if (fecha.isBefore(LocalDateTime.now())) {
		            model.addAttribute("error", "La fecha y hora de la reserva no puede ser anterior a la fecha actual.");
		            return "reservar"; 
		        }

		        LocalTime hora = fecha.toLocalTime();
		        LocalTime horaInicio = LocalTime.of(12, 0);
		        LocalTime horaFin = LocalTime.of(22, 0);
		        LocalDate dia = fecha.toLocalDate(); // usa el mismo día que "fecha"
		        LocalDateTime horaConFecha = LocalDateTime.of(dia, hora); // combinas día y hora

		        if (hora.isBefore(horaInicio) || hora.isAfter(horaFin)) {
		            model.addAttribute("error", "La hora de la reserva debe estar entre las 12:00 y las 22:00.");
		            return "reservar"; 
		        }
		       
		        
		        if (fecha.isBefore(LocalDateTime.now().plus(2, ChronoUnit.DAYS))) {
		            model.addAttribute("error", "La reserva debe hacerse con al menos 2 días de antelación.");
		            return "reservar"; 
		        }
		        cita.setCliente(clientereserva);
		        cita.setVehiculo(vehiculo);
		        cita.setFechahora(fecha);
		        cita.setTipo(Estado.PENDIENTE);
vehiculo.setDisponible(false);
servcita.insertarcita(cita,vehiculo);
String correo = clientereserva.getUsuario().getCorreo(); // Asegúrate de tener el correo
String asunto = "Confirmación de solicitud de cita";
String cuerpo = "Hola " + userReserva.getNombre() + ",\n\n"
              + "Tu solicitud de cita para el vehículo \"" + vehiculo.getMarca() + " " + vehiculo.getModelo()
              + "\" ha sido registrada correctamente y se encuentra PENDIENTE de aprobación. Se te notificará cuando sea aceptada o rechazada\n\n"
              + "Fecha y hora solicitada: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n\n"
              + "Gracias por confiar en nosotros.\n\n"
              + "Atentamente,\nEquipo de TryYourCar";

SimpleMailMessage mensaje = new SimpleMailMessage();
mensaje.setTo(correo);
mensaje.setSubject(asunto);
mensaje.setText(cuerpo);

mailSender.send(mensaje);
		       
model.addAttribute("citaId", cita.getId());
		        model.addAttribute("success", "Se ha registrado su solicitud,en breve será procesada por un administrador. Puede cancelarla en su perfil si lo desea");
		        return "reservar"; 
		        
		        
		        
		        
		    }

		  
		    
		

		    @GetMapping("/miperfil")
		    public String verPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		        String username = userDetails.getUsername();
		        Usuario usuario = servusuario.findByUsername(username);
		        model.addAttribute("usuario", usuario);
		        return "miperfil";
		    }

		    @GetMapping("/editarperfil")
		    public String editarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		        String username = userDetails.getUsername();
		        Usuario usuario = servusuario.findByUsername(username);
		        model.addAttribute("usuario", usuario);
		        return "editarPerfil";
		    }

		    @PostMapping("/editarperfil")
		    public String guardarPerfil(@ModelAttribute Usuario usuarioActualizado, 
		                                 @AuthenticationPrincipal UserDetails userDetails, 
		                                 Model model) {
		        String username = userDetails.getUsername();
		        Usuario usuario = servusuario.findByUsername(username);

		        String nombre = usuarioActualizado.getNombre().trim();
		        String apellidos = usuarioActualizado.getApellidos().trim().replaceAll("\\s+", " "); // Reemplaza múltiples espacios por uno solo
		        String correo = usuarioActualizado.getCorreo().trim();
		        String nombreUsuario = usuarioActualizado.getUser().trim(); // Nuevo campo para el nombre de usuario

		        // Validaciones previas (nombre, apellidos y correo)
		        if (nombre.equalsIgnoreCase(usuario.getNombre()) &&
		            apellidos.equalsIgnoreCase(usuario.getApellidos()) &&
		            correo.equalsIgnoreCase(usuario.getCorreo()) &&
		            nombreUsuario.equalsIgnoreCase(usuario.getUser())) {
		            model.addAttribute("mensajeError", "Debes cambiar al menos un campo para actualizar.");
		            return "editarperfil";
		        }

		        if (nombre.isEmpty() && apellidos.isEmpty() && correo.isEmpty() && nombreUsuario.isEmpty()) {
		            model.addAttribute("mensajeError", "Todos los campos son obligatorios.");
		            return "editarperfil";
		        }

		        if (nombre.length() < 2 || nombre.length() > 20) {
		            model.addAttribute("mensajeError", "El nombre debe tener entre 2 y 50 caracteres.");
		            return "editarperfil"; 
		        }

		        if (apellidos.length() < 2 || apellidos.length() > 30) {
		            model.addAttribute("mensajeError", "Los apellidos deben tener entre 2 y 100 caracteres.");
		            return "editarperfil";
		        }

		        if (correo.length() > 100) {
		            model.addAttribute("mensajeError", "El correo electrónico no puede tener más de 100 caracteres.");
		            return "editarperfil"; 
		        }

		        if (!nombre.matches("^[a-zA-Z]+$")) {
		            model.addAttribute("mensajeError", "El nombre solo puede contener letras.");
		            return "editarperfil";
		        }

		        if (!apellidos.matches("^[a-zA-Z\\s]+$")) {
		            model.addAttribute("mensajeError", "Los apellidos solo pueden contener letras y espacios.");
		            return "editarperfil";
		        }

		        if (!correo.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
		            model.addAttribute("mensajeError", "El correo electrónico no es válido.");
		            return "editarperfil"; 
		        }

		        if (!correo.trim().equalsIgnoreCase(usuario.getCorreo())) {
		            if (servusuario.existeNEmail(correo) == 1) {
		                model.addAttribute("mensajeError", "El correo electrónico ya está registrado.");
		                return "editarperfil";
		            }
		        }

		        // Validaciones para el nombre de usuario
		        if (nombreUsuario.isEmpty()) {
		            model.addAttribute("mensajeError", "El nombre de usuario no puede estar vacío.");
		            return "editarperfil";
		        }

		        if (nombreUsuario.length() < 4 || nombreUsuario.length() > 20) {
		            model.addAttribute("mensajeError", "El nombre de usuario debe tener entre 4 y 20 caracteres.");
		            return "editarperfil";
		        }

		        if (!nombreUsuario.matches("^[a-zA-Z0-9_]+$")) {
		            model.addAttribute("mensajeError", "El nombre de usuario solo puede contener letras, números y guiones bajos.");
		            return "editarperfil";
		        }

		        if (!nombreUsuario.equalsIgnoreCase(usuario.getUser())) {
		            if (servusuario.exiteUser(nombreUsuario) == 1) {
		                model.addAttribute("mensajeError", "El nombre de usuario ya está registrado.");
		                return "editarperfil";
		            }
		        }

		        // Actualización del usuario
		        usuario.setNombre(nombre.toLowerCase());
		        usuario.setApellidos(apellidos.toLowerCase());
		        usuario.setCorreo(correo.toLowerCase());
		        usuario.setUser(nombreUsuario.toLowerCase()); // Actualizando el nombre de usuario

		        servusuario.insertarUser(usuario);

		        model.addAttribute("mensajeExito", "Perfil actualizado correctamente.");

		        return "editarperfil"; 
		    }

		    
		    @GetMapping("/valoraciones")
		    public String valoraciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		        String username = userDetails.getUsername();
		        Usuario usuario = servusuario.findByUsername(username);
		        model.addAttribute("usuario", usuario);

		        Cliente cliente = usuario.getCliente();
		        List<CitasDePrueba> todasCitas = servcita.findAll();


		        List<CitasDePrueba> citasCompletadasCliente = todasCitas.stream()
		        		.filter(cita -> Estado.COMPLETADA.equals(cita.getTipo()) && cita.getCliente().equals(cliente) && cita.getEvaluacion()==null)
		            .toList(); 
		        List<CitasDePrueba> citasCompletadasClienteValoradas = todasCitas.stream()
		        		.filter(cita -> Estado.COMPLETADA.equals(cita.getTipo()) && cita.getCliente().equals(cliente) && cita.getEvaluacion()!=null)
		            .toList(); 
		        
		        model.addAttribute("citasCompletadas", citasCompletadasCliente);
		        model.addAttribute("citasCompletadasValoradas", citasCompletadasClienteValoradas);

		        return "valoraciones";
		    }
		    @GetMapping("/valorar/{id}")
		    public String mostrarFormularioValoracion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		        CitasDePrueba cita = servcita.findById(id);

		        if (cita == null) {
		            redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
		            return "redirect:/valoraciones";
		        }

		        model.addAttribute("cita", cita);
		        return "valorar"; 
		    }
		    
		    @PostMapping("/valorar/{id}")
		    public String valorarCita(
		            @PathVariable Long id,
		            @RequestParam("calificacion") int calificacion,
		            @RequestParam("comentario") String comentario,
		            RedirectAttributes redirectAttributes) {

		        CitasDePrueba cita = servcita.findById(id);

		        if (cita == null) {
		            redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
		            return "redirect:/valoraciones";
		        }

		       
		        if (calificacion < 1 || calificacion > 5 || comentario == null || comentario.trim().isEmpty()) {
		            redirectAttributes.addFlashAttribute("error", "Debe seleccionar una calificación entre 1 y 5 y añadir un comentario.");
		            return "redirect:/valorar/" + id;
		        }

		        Evaluacion evaluacion = new Evaluacion();
		        evaluacion.setCalificación(calificacion);
		        evaluacion.setComentario(comentario.trim());
		        evaluacion.setCitadeprueba(cita);
		        evaluacion.setCliente(cita.getCliente());
		        evaluacion.setVehiculo(cita.getVehiculo());

		        servevaluacion.save(evaluacion);

		        redirectAttributes.addFlashAttribute("success", "¡Valoración enviada con éxito!");
		        return "redirect:/valoraciones";
		    }
		    @GetMapping("/verdetallesvaloracion/{id}")
		    public String verDetallesValoracion(@PathVariable Long id, Model model) {
		        Evaluacion evaluacion = servevaluacion.findById(id);

		        if (evaluacion == null) {
		            return "redirect:/valoraciones?error=true";
		        }

		        model.addAttribute("evaluacion", evaluacion);
		        return "detalle_valoracion";
		    }

		    
@GetMapping("/miscitas")
public String verMisCitas(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String rangoFecha,
        Model model) {

    String username = userDetails.getUsername();
    Usuario usuario = servusuario.findByUsername(username);
    Cliente cliente = usuario.getCliente();


    List<CitasDePrueba> todasCitas = servcita.findByCliente(cliente);

    List<CitasDePrueba> citasFiltradas = todasCitas;

    if (estado != null && !estado.equals("TODOS") && !estado.isEmpty()) {
        citasFiltradas = citasFiltradas.stream()
                .filter(cita -> cita.getTipo().name().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

   
    if (rangoFecha != null && !rangoFecha.equals("TODOS") && !rangoFecha.isEmpty()) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (rangoFecha) {
            case "2DIAS":
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> cita.getFechahora().isAfter(now.minusDays(2)))
                        .collect(Collectors.toList());
                break;
            case "SEMANA":
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> cita.getFechahora().isAfter(now.minusWeeks(1)))
                        .collect(Collectors.toList());
                break;
            case "MES":
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> cita.getFechahora().isAfter(now.minusMonths(1)))
                        .collect(Collectors.toList());
                break;
            case "AÑO":
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> cita.getFechahora().isAfter(now.minusYears(1)))
                        .collect(Collectors.toList());
                break;
            default:
                break;
        }
    }

    // Agregar los datos al modelo para usarlos en la vista
    model.addAttribute("rangoFecha", rangoFecha);
    model.addAttribute("estadoSeleccionado", estado);
    model.addAttribute("citas", citasFiltradas);
    model.addAttribute("estadoEnum", Estado.values());  // Pasamos todos los valores de Estado

    // Agregar los valores específicos de estado para filtrar o mostrar iconos
    model.addAttribute("ESTADO_ACEPTADA", Estado.ACEPTADA);
    model.addAttribute("ESTADO_COMPLETADA", Estado.COMPLETADA);
    model.addAttribute("ESTADO_PENDIENTE", Estado.PENDIENTE);
    model.addAttribute("ESTADO_CANCELADA", Estado.CANCELADA);
    model.addAttribute("ESTADO_RECHAZADA", Estado.RECHAZADA);

    return "miscitas";
}

		    @PostMapping("/cancelarcita")
		    public String cancelarCita(@RequestParam Long id,RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
		        CitasDePrueba cita = servcita.findById(id);
		        String username = userDetails.getUsername();
		        Usuario usuario = servusuario.findByUsername(username);
		        Cliente cliente = usuario.getCliente();
		        LocalDateTime fecha= LocalDateTime.now();
		        if (cita != null && (cita.getTipo() == Estado.PENDIENTE || cita.getTipo() == Estado.ACEPTADA)
		            && cita.getFechahora().isAfter(LocalDateTime.now())) {
		            cita.setTipo(Estado.CANCELADA);
		            servcita.insertarcita(cita, cita.getVehiculo());
		        }
		        String correo = cliente.getUsuario().getCorreo(); // Asegúrate de tener el correo
		        String asunto = "Confirmación de cancelacion de cita";
		        String cuerpo = "Hola " + cliente.getUsuario().getNombre() + ",\n\n"
		                      + "Tu cita para el vehículo \"" + cita.getVehiculo().getMarca() + " " +  cita.getVehiculo().getModelo()
		                      + "\" ha sido cancelada correctamente. Gracais por confiar en noisotros\n\n"
		                      + "Atentamente,\nEquipo de TryYourCar";

		        SimpleMailMessage mensaje = new SimpleMailMessage();
		        mensaje.setTo(correo);
		        mensaje.setSubject(asunto);
		        mensaje.setText(cuerpo);

		        mailSender.send(mensaje);
		        redirectAttributes.addFlashAttribute("mensajeExito", "La cita se ha cancelado correctamente.");
		        return "redirect:/miscitas";
		    }

	                           


	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

