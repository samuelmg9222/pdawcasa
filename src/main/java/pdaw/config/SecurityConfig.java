package pdaw.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;

import pdaw.modelo.Usuario;
import pdaw.services.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired 
    UsuarioService servusuario;
  
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            	      .requestMatchers("/", "/index","/vertodos","/regexitoso","/detalles","/detalles/**", "/registro", "/images/**", "/nuevocliente", "/registrar-cliente","/login","/recuperarcontraseña","/paginarestaurar","/verificarcodigo","/recuperar-contrasena","/verificar-codigo", "/imagenes/vehiculos/**").permitAll()
            	      .requestMatchers("/reservar","/reservar/**").hasAnyRole("CLIENTE")
                      .requestMatchers("/personal").hasAnyRole("ADMIN", "PERSONAL") 
                      .requestMatchers("/").hasAnyRole("ADMIN") 
                      .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    switch (role) {
                        case "ROLE_ADMIN":
                            response.sendRedirect("/admin");
                            break;
                        case "ROLE_PERSONAL":
                            response.sendRedirect("/personal");
                            break;
                        case "ROLE_CLIENTE":
                            response.sendRedirect("/vertodos");
                            break;
                        default:
                            response.sendRedirect("/login");
                            break;
                    }
                })
            )
            
            .logout(logout -> logout
                .logoutSuccessHandler((request, response, authentication) -> {
                    request.getSession().invalidate();
                    response.sendRedirect("/");
                })
                .permitAll()
            )

            .sessionManagement(session -> session
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
            )
            
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/access-denied");
                })
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDetails> users = new ArrayList<>();

        // Agregar el usuario administrador
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();
        users.add(admin);

        // Recuperar todos los usuarios desde el servicio
        List<Usuario> todos = servusuario.findAll();

        // Filtrar el usuario administrador y no agregarlo nuevamente
        String correoAdmin = "admininstrador@gmail.com";
        Usuario administrador = servusuario.findByCorreo(correoAdmin);
        
        // Iterar sobre todos los usuarios excepto el administrador
        for (Usuario t : todos) {
            if (!t.equals(administrador)) { // Evitar agregar el usuario administrador
                UserDetails user;
                if (t.getCliente() == null) {
                    // Usuario de tipo "PERSONAL"
                    user = User.withUsername(t.getUser())
                            .password(passwordEncoder().encode(t.getContraseña()))
                            .roles("PERSONAL")
                            .build();
                } else {
                    // Usuario de tipo "CLIENTE"
                    user = User.withUsername(t.getUser())
                            .password(passwordEncoder().encode(t.getContraseña()))
                            .roles("CLIENTE")
                            .build();
                }
                users.add(user);
            }
        }

        // Devolver el servicio de detalles de usuario con los usuarios registrados
        return new InMemoryUserDetailsManager(users);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
