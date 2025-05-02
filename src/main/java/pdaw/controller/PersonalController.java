package pdaw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pdaw.modelo.*;
import pdaw.services.*;

@Controller
public class PersonalController {
	@Autowired
	UsuarioService servusuario;
	@Autowired
	ClienteService servcliente;
@Autowired
VehiculoService servvehiculo;
@Autowired
EvaluacionService servevaluacion;
@Autowired
CitaDePruebaService servcita;
@Autowired
private JavaMailSender mailSender;

private final String UPLOAD_DIR = "src/main/resources/static/coches/";

@GetMapping("/personal")
public String mostrarFormulario(Model model) {
    model.addAttribute("vehiculo", new Vehiculo());
    return "personal";
}

@PostMapping("/personal/guardarvehiculo")
public String guardarVehiculo(@ModelAttribute Vehiculo vehiculo, Model model, @RequestParam("fotoBase64") String fotoBase64) {
	 try {
	        
	        if (fotoBase64.isEmpty()) {
	            model.addAttribute("error", "La foto no puede estar vacía.");
	            return "redirect:/personal";
	        }

	        
	        byte[] imageBytes = Base64.getDecoder().decode(fotoBase64);
	        
	        
	        String nombreArchivo = System.currentTimeMillis() + ".jpg";
	        String uploadDir = "src/main/resources/static/images/coches/";

	        
	        Path path = Paths.get(uploadDir);
	        if (!Files.exists(path)) {
	            Files.createDirectories(path);
	        }

	        
	        Path rutaCompleta = path.resolve(nombreArchivo);
	        Files.write(rutaCompleta, imageBytes);

	        
	        String fotoUrl = "/images/coches/" + nombreArchivo;

	       
	       
	        vehiculo.setFoto(fotoUrl);

	      
	        servvehiculo.insertarVehiculo(vehiculo);

	        return "redirect:/personal?exito";
	    } catch (IOException e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Hubo un error al guardar la foto.");
	        return "redirect:/personal";
	    }
   

}


   
@GetMapping("/gestiondecitas")
public String verMisCitas(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String rangoFecha,
        @RequestParam(required = false) String orden,
        Model model) {

    List<CitasDePrueba> todasCitas = servcita.findAllPendiente();
    List<CitasDePrueba> citasFiltradas = todasCitas;

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

    if (orden != null && !orden.isEmpty()) {
        switch (orden) {
            case "FECHA_ASC":
                citasFiltradas.sort(Comparator.comparing(CitasDePrueba::getFechahora));
                break;
            case "FECHA_DESC":
                citasFiltradas.sort(Comparator.comparing(CitasDePrueba::getFechahora).reversed());
                break;
          
            default:
                break;
        }
    }

    model.addAttribute("rangoFecha", rangoFecha);
    model.addAttribute("estadoSeleccionado", estado);
    model.addAttribute("ordenSeleccionado", orden);
    model.addAttribute("citas", citasFiltradas);
    model.addAttribute("estadoEnum", Estado.values());

    model.addAttribute("ESTADO_ACEPTADA", Estado.ACEPTADA);
    model.addAttribute("ESTADO_COMPLETADA", Estado.COMPLETADA);
    model.addAttribute("ESTADO_PENDIENTE", Estado.PENDIENTE);
    model.addAttribute("ESTADO_CANCELADA", Estado.CANCELADA);
    model.addAttribute("ESTADO_RECHAZADA", Estado.RECHAZADA);

    return "gestiondecitas";
}

		    @PostMapping("/rechazarcita")
		    public String cancelarCita(@RequestParam Long id,RedirectAttributes redirectAttributes) {
		        CitasDePrueba cita = servcita.findById(id);
		        if (cita != null && (cita.getTipo() == Estado.PENDIENTE || cita.getTipo() == Estado.ACEPTADA)
		            && cita.getFechahora().isAfter(LocalDateTime.now())) {
		            cita.setTipo(Estado.RECHAZADA);
		            servcita.insertarcita(cita, cita.getVehiculo());
		        }
		        String correo = cita.getCliente().getUsuario().getCorreo(); // Asegúrate de tener el correo
		        String asunto = "Confirmación de cancelacion de cita";
		        String cuerpo = "Hola " + cita.getCliente().getUsuario().getNombre() + ",\n\n"
		                      + "Tu cita para el vehículo \"" + cita.getVehiculo().getMarca() + " " +  cita.getVehiculo().getModelo()
		                      + "\" ha sido cancelada por nuestro equipo."
		                      + "Disuculpe las molestias y Gracais por confiar en nosotros\n\n"
		                      + "Atentamente,\nEquipo de TryYourCar";

		        SimpleMailMessage mensaje = new SimpleMailMessage();
		        mensaje.setTo(correo);
		        mensaje.setSubject(asunto);
		        mensaje.setText(cuerpo);

		        mailSender.send(mensaje);
		        redirectAttributes.addFlashAttribute("mensajeExito", "La cita se ha rechazado correctamente.");
		        return "redirect:/gestiondecitas";

		    }
		    @PostMapping("/aceptarcita")
		    public String aceptarCita(@RequestParam Long id,RedirectAttributes redirectAttributes) {
		        CitasDePrueba cita = servcita.findById(id);
		        if (cita != null && (cita.getTipo() == Estado.PENDIENTE || cita.getTipo() == Estado.ACEPTADA)
		            && cita.getFechahora().isAfter(LocalDateTime.now())) {
		            cita.setTipo(Estado.ACEPTADA);
		            servcita.insertarcita(cita, cita.getVehiculo());
		        }
		        String correo = cita.getCliente().getUsuario().getCorreo(); 
		        String asunto = "Confirmación de cancelacion de cita";
		        String cuerpo = "Hola " + cita.getCliente().getUsuario().getNombre() + ",\n\n"
		                      + "Tu cita para el vehículo \"" + cita.getVehiculo().getMarca() + " " +  cita.getVehiculo().getModelo()
		                      + "\" ha sido aceptada por nuestro equipo. Recuerde estar ahi a las " + cita.getFechahora()
		                      + "Si no puede acudir por algun casual, cancelala en su perfil\n\n"
		                      + "Gracais por confiar en nosotros\n\n"
		                      + "Atentamente,\nEquipo de TryYourCar";

		        SimpleMailMessage mensaje = new SimpleMailMessage();
		        mensaje.setTo(correo);
		        mensaje.setSubject(asunto);
		        mensaje.setText(cuerpo);

		        mailSender.send(mensaje);
		        redirectAttributes.addFlashAttribute("mensajeExito", "La cita se ha rechazado correctamente.");
		        return "redirect:/gestiondecitas";

		    }
		    
		    
		    @GetMapping("/cliente/{id}")
		    public String verMisCitas(
		    		@RequestParam(required = false) String conValoracion,
		            @AuthenticationPrincipal UserDetails userDetails,
		            @RequestParam(required = false) String estado,
		            @RequestParam(required = false) String rangoFecha,
		            @RequestParam(required = false) String rangoFechaEv,
		            @PathVariable Long id,
		            Model model) {

		        
		        Cliente cliente = servcliente.findById(id);


		        List<CitasDePrueba> todasCitas = servcita.findByCliente(cliente);
		        List <Evaluacion>todasEv = servevaluacion.findByCliente(cliente);
		        List<CitasDePrueba> citasFiltradas = todasCitas;
		        List <Evaluacion>EvFiltradas=todasEv;
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
		        if (rangoFechaEv != null && !rangoFechaEv.equals("TODOS") && !rangoFechaEv.isEmpty()) {
		            LocalDateTime now = LocalDateTime.now();
		            
		            switch (rangoFechaEv) {
		                case "2DIAS":
		                	EvFiltradas = EvFiltradas.stream()
		                            .filter(ev -> ev.getFehcahora().isAfter(now.minusDays(2)))
		                            .collect(Collectors.toList());
		                    break;
		                case "SEMANA":
		                	EvFiltradas = EvFiltradas.stream()
		                            .filter(ev -> ev.getFehcahora().isAfter(now.minusWeeks(1)))
		                            .collect(Collectors.toList());
		                    break;
		                case "MES":
		                	EvFiltradas = EvFiltradas.stream()
		                            .filter(ev -> ev.getFehcahora().isAfter(now.minusMonths(1)))
		                            .collect(Collectors.toList());
		                    break;
		                case "AÑO":
		                	EvFiltradas = EvFiltradas.stream()
		                            .filter(ev -> ev.getFehcahora().isAfter(now.minusYears(1)))
		                            .collect(Collectors.toList());
		                    break;
		                default:
		                    break;
		            }
		        }
		       
		        model.addAttribute("rangoFecha", rangoFecha);
		        model.addAttribute("rangoFechaEv", rangoFechaEv);
		        model.addAttribute("estadoSeleccionado", estado);
		        model.addAttribute("citas", citasFiltradas);
		        model.addAttribute("ev", EvFiltradas);
		        model.addAttribute("estadoEnum", Estado.values());  
		        model.addAttribute("conValoracion", conValoracion);
		       
		        model.addAttribute("ESTADO_ACEPTADA", Estado.ACEPTADA);
		        model.addAttribute("ESTADO_COMPLETADA", Estado.COMPLETADA);
		        model.addAttribute("ESTADO_PENDIENTE", Estado.PENDIENTE);
		        model.addAttribute("ESTADO_CANCELADA", Estado.CANCELADA);
		        model.addAttribute("ESTADO_RECHAZADA", Estado.RECHAZADA);

		        return "cliente";
		    }	    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    }


















































/*
@PostMapping("/personal/guardarvehiculo")
public String guardarVehiculo(@ModelAttribute Vehiculo vehiculo,
                              @RequestParam("foto") MultipartFile foto,
                              Model model) {

   
    if (vehiculo.getMarca() == null || !vehiculo.getMarca().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,30}")) {
        model.addAttribute("error", "La marca solo puede contener letras y debe tener entre 2 y 30 caracteres.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (vehiculo.getModelo() == null || vehiculo.getModelo().length() < 1 || vehiculo.getModelo().length() > 30) {
        model.addAttribute("error", "El modelo es obligatorio y debe tener entre 1 y 30 caracteres.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (vehiculo.getAñofabricacion() < 1900 || vehiculo.getAñofabricacion() > 2099) {
        model.addAttribute("error", "El año de fabricación debe estar entre 1900 y 2099.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (vehiculo.getKilometraje() < 0) {
        model.addAttribute("error", "El kilometraje no puede ser negativo.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (vehiculo.getPrecio() < 0) {
        model.addAttribute("error", "El precio no puede ser negativo.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (vehiculo.getColor() == null || !vehiculo.getColor().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,20}")) {
        model.addAttribute("error", "El color solo puede contener letras y debe tener entre 2 y 20 caracteres.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    

    if (vehiculo.getDescripcion() == null || vehiculo.getDescripcion().length() < 10) {
        model.addAttribute("error", "La descripción es obligatoria y debe tener al menos 10 caracteres.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    if (foto == null || foto.isEmpty()) {
        model.addAttribute("error", "Debe subir una foto del vehículo.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    try {
        Files.createDirectories(Paths.get("src/main/resources/static/coches/"));

        String nombreArchivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
        Path rutaCompleta = Paths.get("src/main/resources/static/coches/" + nombreArchivo);

        Files.copy(foto.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        vehiculo.setFoto("/coches/" + nombreArchivo);

    } catch (IOException e) {
        e.printStackTrace();
        model.addAttribute("error", "Error al subir la imagen.");
        model.addAttribute("vehiculo", vehiculo);
        return "personal";
    }

    servvehiculo.insertarVehiculo(vehiculo);
    return "redirect:/personal?exito";
}
*/

