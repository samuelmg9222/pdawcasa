package pdaw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import pdaw.modelo.CitasDePrueba;
import pdaw.modelo.Evaluacion;
import pdaw.modelo.TipoVehiculo;
import pdaw.modelo.Usuario;
import pdaw.modelo.Vehiculo;
import pdaw.services.CitaDePruebaService;
import pdaw.services.ClienteService;
import pdaw.services.EvaluacionService;
import pdaw.services.UsuarioService;
import pdaw.services.VehiculoService;

@Controller
public class AdminController {
	
	@Autowired
	VehiculoService servvehiculo; 
	@Autowired
	UsuarioService servusuario;
	@Autowired
	ClienteService servcliente;
	
	@Autowired
	CitaDePruebaService servcita;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	EvaluacionService servevaluacion;
	
	@GetMapping("/admin")
	public String adminPage() {
	    return "admin"; 
	}
	
	@GetMapping("/gestiondevehiculos")
	public String gestiondevehiculos(Model model) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "gestiondevehiculos";
	}
	
	
	@GetMapping("/gestiondepersonal")
	public String gestiondepersonal(Model model) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "gestiondepersonal"; 
	}
	@GetMapping("/anadirPersonal")
	public String mostrarFormulario(Model model) {
	    if (!model.containsAttribute("usuario")) {
	        model.addAttribute("usuario", new Usuario());
	    }

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String rol = auth.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .findFirst()
	            .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "anadirPersonal";
	}

	@PostMapping("/agregarPersonal")
	public String agregarPersonal(@ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    // Validar nombre (solo letras, entre 3 y 50)
	    if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
	        result.rejectValue("nombre", "error.nombre", "El nombre no puede estar vacío");
	    } else if (!usuario.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$")) {
	        result.rejectValue("nombre", "error.nombre", "El nombre solo puede contener letras y espacios (3-50 caracteres)");
	    }

	    // Validar apellidos
	    if (usuario.getApellidos() == null || usuario.getApellidos().isBlank()) {
	        result.rejectValue("apellidos", "error.apellidos", "Los apellidos no pueden estar vacíos");
	    } else if (!usuario.getApellidos().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,100}$")) {
	        result.rejectValue("apellidos", "error.apellidos", "Los apellidos solo pueden contener letras y espacios (3-100 caracteres)");
	    }

	    // Validar correo
	    if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
	        result.rejectValue("correo", "error.correo", "El correo no puede estar vacío");
	    } else if (!usuario.getCorreo().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	        result.rejectValue("correo", "error.correo", "Formato de correo no válido");
	    } else if (servusuario.existeNEmail(usuario.getCorreo())==1) {
	        result.rejectValue("correo", "error.correo", "El correo ya está registrado");
	    }

	    // Validar fecha de nacimiento
	    if (usuario.getFechanac() == null) {
	        result.rejectValue("fechanac", "error.fechanac", "La fecha de nacimiento no puede estar vacía");
	    } else {
	        Calendar hoy = Calendar.getInstance();
	        Calendar fechaNacimiento = Calendar.getInstance();
	        fechaNacimiento.setTime(usuario.getFechanac());

	        hoy.add(Calendar.YEAR, -18); // restamos 18 años a la fecha actual

	        if (fechaNacimiento.after(hoy.getTime())) {
	            result.rejectValue("fechanac", "error.fechanac", "Debes tener al menos 18 años");
	        }
	    }

	    // Validar nombre de usuario
	    if (usuario.getUser() == null || usuario.getUser().isBlank()) {
	        result.rejectValue("user", "error.user", "El nombre de usuario no puede estar vacío");
	    } else if (!usuario.getUser().matches("^[a-zA-Z0-9_]{3,20}$")) {
	        result.rejectValue("user", "error.user", "Debe tener entre 3 y 20 caracteres alfanuméricos (sin espacios)");
	    } else if (servusuario.exiteUser(usuario.getUser())==0) {
	        result.rejectValue("user", "error.user", "Ese nombre de usuario ya está en uso");
	    }

	    // Validar contraseña
	    if (usuario.getContraseña() == null || usuario.getContraseña().isBlank()) {
	        result.rejectValue("contraseña", "error.contraseña", "La contraseña no puede estar vacía");
	    } else if (usuario.getContraseña().length() < 6) {
	        result.rejectValue("contraseña", "error.contraseña", "La contraseña debe tener al menos 6 caracteres");
	    } else if (usuario.getContraseña().matches("^[0-9]*$")) {
	        result.rejectValue("contraseña", "error.contraseña", "No puede ser solo numérica");
	    }

	    // Si hay errores, volver al formulario
	    if (result.hasErrors()) {
	        result.getFieldErrors().forEach(error -> {
	            System.out.println("Error en campo: " + error.getField() + " -> " + error.getDefaultMessage());
	        });
	        model.addAttribute("errorGlobal", "Hay errores en el formulario. Revisa los campos.");
	        return "anadirPersonal";
	    }

	   
	    try {
	        servusuario.insertarUser(usuario);
	      /*  if (!inMemoryUserDetailsManager.userExists(usuario.getUser())) {
	            UserDetails userDetails = User.withUsername(usuario.getUser())
	                                          .password(passwordEncoder.encode(usuario.getContraseña()))
	                                          .roles(usuario.getCliente() != null ? "CLIENTE" : "PERSONAL")
	                                          .build();
	            inMemoryUserDetailsManager.createUser(userDetails);
	        }*/
	        redirectAttrs.addFlashAttribute("exito", "Personal añadido correctamente.");
	        return "redirect:/anadirPersonal";
	    } catch (Exception e) {
	        model.addAttribute("errorGlobal", "Ocurrió un error al guardar el personal.");
	        return "anadirPersonal";
	    }
	}

@GetMapping("/editarpersonal")
public String editarpersonal(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "id") String sortBy,
                             @RequestParam(defaultValue = "asc") String order,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
                             Model model) {

    Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Usuario> paginaPersonalBD = servusuario.listarTodo(pageable);

    // Filtrar usuarios que NO son clientes
    List<Usuario> filtrados = paginaPersonalBD.getContent()
                                              .stream()
                                              .filter(u -> u.getCliente() == null)
                                              .toList();

    Page<Usuario> paginaPersonal = new PageImpl<>(filtrados, pageable, filtrados.size());

    if (desde == null) desde = LocalDate.of(1900, 1, 1);
    if (hasta == null) hasta = LocalDate.now();

    model.addAttribute("paginaPersonal", paginaPersonal);
    model.addAttribute("personales", paginaPersonal.getContent());
    model.addAttribute("paginaActual", page);
    model.addAttribute("totalPaginas", paginaPersonal.getTotalPages());
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortDir", order);
    model.addAttribute("desde", desde);
    model.addAttribute("hasta", hasta);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
    String rol = auth.getAuthorities().stream()
                     .map(GrantedAuthority::getAuthority)
                     .findFirst()
                     .orElse("ROLE_ANONYMOUS");

    model.addAttribute("rol", rol);
    return "editarpersonal";
}

	@GetMapping("/eliminarpersonal")
	public String eliminarpersonal(@RequestParam(defaultValue = "0") int page,
	                               @RequestParam(defaultValue = "10") int size,
	                               @RequestParam(defaultValue = "id") String sortBy,
	                               @RequestParam(defaultValue = "asc") String order,
	                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
	                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
	                               Model model) {

	    Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	    Pageable pageable = PageRequest.of(page, size, sort);

	    // Obtener todos los usuarios paginados
	    Page<Usuario> paginaPersonalBD = servusuario.listarTodo(pageable);

	    // Filtrar usuarios que no tengan cliente (es decir, solo personales)
	    List<Usuario> filtrados = paginaPersonalBD.getContent()
	                                              .stream()
	                                              .filter(u -> u.getCliente() == null)
	                                              .toList();

	    // Crear nueva página con los usuarios filtrados
	    Page<Usuario> paginaPersonal = new PageImpl<>(filtrados, pageable, filtrados.size());

	    model.addAttribute("paginaPersonal", paginaPersonal);
	    model.addAttribute("personales", paginaPersonal.getContent());
	    model.addAttribute("paginaActual", page);
	    model.addAttribute("totalPaginas", paginaPersonal.getTotalPages());
	    model.addAttribute("sortBy", sortBy);
	    model.addAttribute("sortDir", order);
	    model.addAttribute("desde", desde);
	    model.addAttribute("hasta", hasta);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "eliminarpersonal";
	}
	
	@PostMapping("/eliminarpersonalact/{id}")
	public String eliminarpersonalact(@PathVariable("id") Long id, RedirectAttributes redirectAttrs, Model model) {
	    Usuario usuario = servusuario.buscarPorId(id);
	    if (usuario == null || usuario.getCliente() != null) {
	        redirectAttrs.addFlashAttribute("error", "No se ha podido eliminar al usuario (es un cliente o no existe).");
	        return "redirect:/eliminarpersonal";
	    }

	    servusuario.eliminar(usuario);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    redirectAttrs.addFlashAttribute("exito", "Usuario eliminado correctamente.");
	    return "redirect:/eliminarpersonal";
	}
	
	@GetMapping("/listar")
	public String listarPersonal(@RequestParam(defaultValue = "0") int page,
	                             @RequestParam(defaultValue = "10") int size,
	                             @RequestParam(defaultValue = "id") String sortBy,
	                             @RequestParam(defaultValue = "asc") String order,
	                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
	                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
	                             Model model) {

	    Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<Usuario> paginaPersonalBD;

	    if (desde != null && hasta != null) {
	        paginaPersonalBD = servusuario.buscarPorFechaNacimientoEntre(desde, hasta, pageable);
	    } else {
	        paginaPersonalBD = servusuario.listarTodo(pageable);
	    }

	    // Filtrar solo los que NO tienen cliente (o sea, personales)
	    List<Usuario> filtrados = paginaPersonalBD.getContent()
	                                              .stream()
	                                              .filter(u -> u.getCliente() == null)
	                                              .toList();

	    Page<Usuario> paginaPersonal = new PageImpl<>(filtrados, pageable, filtrados.size());

	    if (desde == null) desde = LocalDate.of(1900, 1, 1);
	    if (hasta == null) hasta = LocalDate.now();

	    model.addAttribute("paginaPersonal", paginaPersonal);
	    model.addAttribute("personales", paginaPersonal.getContent());
	    model.addAttribute("paginaActual", page);
	    model.addAttribute("totalPaginas", paginaPersonal.getTotalPages());
	    model.addAttribute("sortBy", sortBy);
	    model.addAttribute("sortDir", order);
	    model.addAttribute("desde", desde);
	    model.addAttribute("hasta", hasta);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "listar";
	}
	
	@GetMapping("/listarclientes")
	public String listarclientes(@RequestParam(defaultValue = "0") int page,
	                             @RequestParam(defaultValue = "10") int size,
	                             @RequestParam(defaultValue = "id") String sortBy,
	                             @RequestParam(defaultValue = "asc") String order,
	                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
	                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
	                             Model model) {

	    Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<Usuario> paginaPersonalBD = servusuario.listarTodo(pageable);

	    // Filtrar usuarios que NO son clientes
	    List<Usuario> filtrados = paginaPersonalBD.getContent()
	                                              .stream()
	                                              .filter(u -> u.getCliente() != null)
	                                              .toList();

	    Page<Usuario> paginaPersonal = new PageImpl<>(filtrados, pageable, filtrados.size());

	    if (desde == null) desde = LocalDate.of(1900, 1, 1);
	    if (hasta == null) hasta = LocalDate.now();

	    model.addAttribute("paginaPersonal", paginaPersonal);
	    model.addAttribute("personales", paginaPersonal.getContent());
	    model.addAttribute("paginaActual", page);
	    model.addAttribute("totalPaginas", paginaPersonal.getTotalPages());
	    model.addAttribute("sortBy", sortBy);
	    model.addAttribute("sortDir", order);
	    model.addAttribute("desde", desde);
	    model.addAttribute("hasta", hasta);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    return "listarclientes";
	}
	@PostMapping("/generarInformeCliente")
	public void generarInformeCliente(@RequestParam("idusuario") Long idusuario, HttpServletResponse response,Model model) throws IOException, DocumentException {
	    Usuario Usercliente = servusuario.buscarPorId(idusuario);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    if (Usercliente.getCliente() == null) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cliente no encontrado.");
	        return;
	    }

	    List<CitasDePrueba> citas = Usercliente.getCliente().getCitasdeprueba();
	    if(citas.isEmpty()) System.out.print("no hay");
	    List<Evaluacion> valoraciones = Usercliente.getCliente().getEvaluaciones();

	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=informe_cliente_" + Usercliente.getId() + ".pdf");

	    Document document = new Document();
	    PdfWriter.getInstance(document, response.getOutputStream());
	    document.open();

	    
	    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	    Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

	    document.add(new Paragraph("Informe del Cliente", titleFont));
	    document.add(new Paragraph("Nombre: " + Usercliente.getNombre() + " " + Usercliente.getApellidos(), normalFont));
	    document.add(new Paragraph("Correo: " + Usercliente.getCorreo(), normalFont));
	    document.add(new Paragraph("Usuario: " + Usercliente.getUser(), normalFont));
	    document.add(new Paragraph("Fecha de Nacimiento: " +Usercliente.getFechanac(), normalFont));
	    document.add(new Paragraph(" ")); 

	    document.add(new Paragraph("Citas de Prueba:", titleFont));
	    for (CitasDePrueba cita : citas) {
	        document.add(new Paragraph(" - Fecha: " + cita.getFechahora() + ", Vehículo: " + cita.getVehiculo().getMarca() + " " + cita.getVehiculo().getModelo()+" Estado:"+ cita.getTipo(), normalFont));
	    }

	    document.add(new Paragraph(" ")); 

	    document.add(new Paragraph("Valoraciones:", titleFont));
	    for (Evaluacion val : valoraciones) {
	        document.add(new Paragraph(" - Vehículo: " + val.getVehiculo().getMarca() + " " + val.getVehiculo().getModelo() +   ", Comentario: " + val.getComentario(), normalFont));
	    }

	    document.close();
	}
	@GetMapping("/editarpersonalact/{id}")
	public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
	    Usuario usuario = servusuario.buscarPorId(id);
	    if (usuario== null) {
	        return "redirect:/editarpersonal"; 
	    }
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    model.addAttribute("usuario", usuario);
	    return "formulario-edicion"; 
	}
	@PostMapping("/editarpersonalact/{id}")
	public String guardarEdicion(@PathVariable("id") Long id,
	                             @ModelAttribute Usuario usuarioActualizado,
	                             Model model) {

	    Usuario original = servusuario.buscarPorId(id);

	    if (original == null) {
	        model.addAttribute("error", "El usuario no existe.");
	        return "formulario-edicion";
	    }
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    List<String> errores = new ArrayList<>();

	    // Validaciones

	    // Validar nombre de usuario
	    if (usuarioActualizado.getUser() == null || 
	        !usuarioActualizado.getUser().matches("^[a-zA-Z0-9]{4,20}$")) {
	        errores.add("El nombre de usuario debe tener entre 4 y 20 caracteres, solo letras y números.");
	    } else {
	        // Verificar si el nombre de usuario ya existe
	        Usuario usuarioExistente = servusuario.findByUser(usuarioActualizado.getUser());
	        if (usuarioExistente != null && !usuarioExistente.getId().equals(id)) {
	            errores.add("El nombre de usuario ya está en uso.");
	        }
	    }

	    // Validar nombre
	    if (usuarioActualizado.getNombre() == null ||
	        !usuarioActualizado.getNombre().matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ]+(\\s[a-zA-ZÁÉÍÓÚáéíóúñÑ]+)*$")) {
	        errores.add("El nombre solo puede contener letras y espacios, y debe tener entre 2 y 30 caracteres.");
	    }

	    // Validar apellidos
	    if (usuarioActualizado.getApellidos() == null ||
	        !usuarioActualizado.getApellidos().matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ\\s]{2,50}$")) {
	        errores.add("Los apellidos solo pueden contener letras y espacios, entre 2 y 50 caracteres.");
	    }

	    // Validar fecha de nacimiento
	    if (usuarioActualizado.getFechanac() == null) {
	        errores.add("La fecha de nacimiento es obligatoria.");
	    } else {
	        Calendar hoy = Calendar.getInstance();
	        Calendar fechaMin = (Calendar) hoy.clone();
	        Calendar fechaMax = (Calendar) hoy.clone();

	        fechaMin.add(Calendar.YEAR, -100);
	        fechaMax.add(Calendar.YEAR, -18);

	        java.sql.Date fechaNac = usuarioActualizado.getFechanac();
	        if (fechaNac.before(fechaMin.getTime()) || fechaNac.after(fechaMax.getTime())) {
	            errores.add("La fecha de nacimiento debe indicar una edad entre 18 y 100 años.");
	        }
	    }

	    // Validar correo electrónico
	    if (usuarioActualizado.getCorreo() == null ||
	        !usuarioActualizado.getCorreo().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	        errores.add("El correo electrónico no es válido.");
	    } else {
	        
	        Usuario usuarioCorreoExistente = servusuario.findByCorreo(usuarioActualizado.getCorreo());
	        if (usuarioCorreoExistente != null && !usuarioCorreoExistente.getId().equals(id)) {
	            errores.add("El correo electrónico ya está en uso.");
	        }
	    }

	    // Si hay errores, mostrar el formulario con los errores
	    if (!errores.isEmpty()) {
	        model.addAttribute("errores", errores);
	        model.addAttribute("usuario", usuarioActualizado);
	        return "formulario-edicion";
	    }

	    // Si todo está bien, actualizamos
	    original.setUser(usuarioActualizado.getUser());
	    original.setNombre(usuarioActualizado.getNombre());
	    original.setApellidos(usuarioActualizado.getApellidos());
	    original.setCorreo(usuarioActualizado.getCorreo());
	    original.setFechanac(usuarioActualizado.getFechanac());

	    servusuario.insertarUser(original);

	    return "redirect:/editarpersonal?exito=true";
	}
	
	
	
	@PostMapping("/actualizar/{id}")
	public String actualizarVehiculo(@PathVariable Long id,
	                                  @ModelAttribute("vehiculo") Vehiculo vehiculo,
	                                  RedirectAttributes redirectAttributes,Model model)  {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    List<String> errores = new ArrayList<>();
	    Vehiculo vehiculoAct = servvehiculo.findById(id);
	    // Año de fabricación: entre 1990 y el año actual
	    int anioActual = Year.now().getValue();
	    if (vehiculo.getAñofabricacion() < 1990 || vehiculo.getAñofabricacion() > anioActual) {
	        errores.add("El año debe estar entre 1990 y " + anioActual + ".");
	    }

	    // Marca: solo letras y espacios
	    if (vehiculo.getMarca() == null || !vehiculo.getMarca().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
	        errores.add("La marca solo puede contener letras.");
	    }

	    // Modelo: letras, números y espacios
	    if (vehiculo.getModelo() == null || !vehiculo.getModelo().matches("^[a-zA-Z0-9\\s-]+$")) {
	        errores.add("El modelo solo puede contener letras y números.");
	    }

	    // Kilometraje: entre 0 y 300000
	    if (vehiculo.getKilometraje() < 0 || vehiculo.getKilometraje() > 300000) {
	        errores.add("El kilometraje debe estar entre 0 y 300000.");
	    }

	    // Precio: entre 3000 y 400000
	    if (vehiculo.getPrecio() < 3000 || vehiculo.getPrecio() > 400000) {
	        errores.add("El precio debe estar entre 3000 y 400000 €.");
	    }

	    // Descripción: máximo 1000 caracteres, letras, números, .,; y espacios
	    if (vehiculo.getDescripcion() == null || vehiculo.getDescripcion().length() > 1000 ||
	        !vehiculo.getDescripcion().matches("^[\\w\\s.,;áéíóúÁÉÍÓÚñÑüÜ-]*$")) {
	        errores.add("La descripción es inválida o demasiado larga (máx. 1000 caracteres).");
	    }

	    // Si hay errores, se devuelven al formulario
	    if (!errores.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errores", errores);
	        redirectAttributes.addFlashAttribute("estado", "error");
	        return "redirect:/editar/" + id; // Redirige al formulario de edición
	    }

	    // --- FORMATEO DE DATOS ---
	    // Marca: trim, eliminar espacios dobles, capitalizar
	    if (vehiculo.getMarca() != null) {
	        String marca = vehiculo.getMarca().trim().replaceAll("[^\\p{L}\\p{N}\\s._-]", "");
	        vehiculo.setMarca(capitalize(marca));
	    }

	    // Modelo: trim, eliminar espacios dobles, capitalizar
	    if (vehiculo.getModelo() != null) {
	        String modelo = vehiculo.getModelo().trim().replaceAll("[^\\p{L}\\p{N}\\s._-]", "");
	        vehiculo.setModelo(capitalize(modelo));
	    }

	    // Descripción: trim y limpiar espacios
	    if (vehiculo.getDescripcion() != null) {
	        String desc = vehiculo.getDescripcion().trim().replaceAll("[^\\p{L}\\p{N}\\s._-]", "");
	        vehiculo.setDescripcion(desc);
	    }

	    try {
	        vehiculo.setId(id);
	        vehiculo.setTipo(vehiculoAct.getTipo());
	        vehiculo.setColor(vehiculoAct.getColor());
	        vehiculo.setFoto(vehiculoAct.getFoto());
	        servvehiculo.insertar(vehiculo); // Guarda los cambios en el vehículo
	        redirectAttributes.addFlashAttribute("mensaje", "Vehículo actualizado correctamente.");
	        redirectAttributes.addFlashAttribute("estado", "exito");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el vehículo.");
	        redirectAttributes.addFlashAttribute("estado", "error");
	    }

	    return "redirect:/editar/" + id; // Redirige a la página de edición después de la actualización
	}
	
	private String capitalize(String input) {
	    if (input.isEmpty()) return input;
	    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
	@GetMapping("/editar/{id}")
	public String editarVehiculo(@PathVariable Long id, Model model) {
	    Vehiculo vehiculo = servvehiculo.findById(id);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    if (vehiculo == null) {
	        return "redirect:/editarvehiculo"; // Redirige a otra página si no se encuentra el vehículo
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

	    model.addAttribute("vehiculo", vehiculo); // El vehículo a editar
	    model.addAttribute("vehiculos", destacados); // Vehículos destacados

	    return "editar"; // Página de formulario de edición
	}


	@PostMapping("/eliminar/{id}")
	public String eliminarVehiculo(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	    try {
	        servvehiculo.eliminarVehiculo(id);  // Llama al servicio para eliminar el vehículo
	        redirectAttributes.addFlashAttribute("mensaje", "Vehículo eliminado exitosamente.");
	        redirectAttributes.addFlashAttribute("estado", "exito");  // Estado para éxito
	        return "redirect:/eliminarvehiculos";  // Devuelve la vista "eliminarvehiculos"
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("mensaje", "No se ha podido eliminar ya que hay citas/evaluaciones relaccionadas");
	    	redirectAttributes.addFlashAttribute("estado", "error");  // Estado para error
	        return "redirect:/eliminarvehiculos"; // Devuelve la misma vista con el mensaje de error
	    }
	}
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
       
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
    return "eliminarvehiculos"; 
}
	@GetMapping("/eliminarvehiculos")
	public String eliminarvehiculos(@RequestParam(defaultValue = "0") int page,
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

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
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
    return "eliminarvehiculos"; 
}
	@GetMapping("/editarvehiculos")
	 public String vereditar(@RequestParam(defaultValue = "0") int page,
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Obtener el rol del usuario (puede haber más de uno, aquí tomamos el primero)
	    String rol = auth.getAuthorities().stream()
	                     .map(GrantedAuthority::getAuthority)
	                     .findFirst()
	                     .orElse("ROLE_ANONYMOUS");

	    model.addAttribute("rol", rol);
	       
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
	    return "editarvehiculos"; 
	}
}
