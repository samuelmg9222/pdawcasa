package pdaw.config;

import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Configuration
public class MailConfig  {

	 @Bean
	   public JavaMailSender javaMailSender() {
	       JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	       
	       // Configuración de Outlook
	       mailSender.setHost("smtp-mail.outlook.com");  
	       mailSender.setPort(587);  
	       mailSender.setUsername("samuelmg92@educastur.es"); 
	       mailSender.setPassword("pabu67_Z");  

	       // Propiedades para conexión segura
	       Properties props = mailSender.getJavaMailProperties();
	       props.put("mail.smtp.auth", "true");  
	       props.put("mail.smtp.starttls.enable", "true");  
	       props.put("mail.smtp.from", "samuelmg92@educastur.es");
	       return mailSender;
	   }
	 

	    
	    public void sendEmail(String to, String subject, String htmlContent) throws MailException, MessagingException {
	        JavaMailSender mailSender = javaMailSender();  
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);  

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(htmlContent, true);  
	        helper.setFrom("samuelmg92@educastur.es");

	        mailSender.send(message);
	    }
	
	}