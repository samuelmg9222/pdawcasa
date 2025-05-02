package pdaw.services;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pdaw.modelo.CitasDePrueba;
import pdaw.modelo.Cliente;
import pdaw.modelo.Estado;
import pdaw.modelo.Vehiculo;
import pdaw.repos.*;

@Service
public class CitaDePruebaService {
	@Autowired
	CitasDePruebaRepos citarepos;
	
	@Autowired
	VehiculoRepos vehiculorepos;
	
	public List<CitasDePrueba> findAll(){
		
		return citarepos.findAll();
		
	}
public List<CitasDePrueba> findAllPendiente(){
	List<CitasDePrueba> todas =citarepos.findAll();
	List<CitasDePrueba> pendientes = new ArrayList<CitasDePrueba>();
	for(CitasDePrueba t: todas) {
		if (t.getTipo() == Estado.PENDIENTE) {
		   pendientes.add(t);
		}
	}
		return pendientes;
		
	}

    public CitasDePrueba findById(Long id) {
    
        return citarepos.findById(id).orElse(null);
      
    }
	public void insertarcita(CitasDePrueba cita, Vehiculo vh) {
		citarepos.saveAndFlush(cita);
		vehiculorepos.saveAndFlush(vh);
		}
	
    @Scheduled(fixedRate = 6000) 
    public void verificarCitas() {
       
        List<CitasDePrueba> citas = citarepos.findByTipo(Estado.ACEPTADA);

        for (CitasDePrueba cita : citas) {
          
            if (cita.getFechahora().isBefore(LocalDateTime.now())) {
             
                cita.setTipo(Estado.COMPLETADA);
                
               
                Vehiculo vehiculo = cita.getVehiculo();
                vehiculo.setDisponible(true);

               
                citarepos.save(cita);
                vehiculorepos.save(vehiculo);
            }
        }
    }
    @Autowired
	private JavaMailSender mailSender;
    @Scheduled(fixedRate = 6000) 
    public void pendientenoaceptada() {
       
        List<CitasDePrueba> citas = citarepos.findByTipo(Estado.PENDIENTE);

        for (CitasDePrueba cita : citas) {
          
        	if (cita.getFechahora().isBefore(LocalDateTime.now().minusHours(2))) {
             
                cita.setTipo(Estado.RECHAZADA);
                
               
                Vehiculo vehiculo = cita.getVehiculo();
                vehiculo.setDisponible(true);

               
                citarepos.save(cita);
                vehiculorepos.save(vehiculo);
                String correo = cita.getCliente().getUsuario().getCorreo();// Asegúrate de tener el correo
                String asunto = "Solicitud de cita de vehiuclo "+ cita.getVehiculo().getMarca()+" "+ cita.getVehiculo().getModelo();
                String cuerpo = "Hola " + cita.getCliente().getUsuario().getNombre() + ",\n\n"
                              + "Tu solicitud de cita para el vehículo \"" + vehiculo.getMarca() + " " + vehiculo.getModelo()
                              + "\" ha sido rechadada debido a que ningun personal ha aceptado su solicitud. Puedes solicitar una nueva sin problemas. Disuclpe las molestias\n\n"
                              + "Gracias por confiar en nosotros.\n\n"
                              + "Atentamente,\nEquipo de TryYourCar";

                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(correo);
                mensaje.setSubject(asunto);
                mensaje.setText(cuerpo);

                mailSender.send(mensaje);
            }
        }
    }
    public List<CitasDePrueba> findByCliente(Cliente cliente) {
        return citarepos.findByCliente(cliente);
    }
}

