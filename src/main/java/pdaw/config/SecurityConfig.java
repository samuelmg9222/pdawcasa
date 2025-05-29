package pdaw.config;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;

import pdaw.modelo.Usuario;
import pdaw.services.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioService servusuario;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index","/vertodos","/regexitoso","/detalles","/detalles/**", "/registro", "/images/**", "/nuevocliente",
                		"/registrar-cliente","/login","/recuperarcontraseña","/paginarestaurar","/verificarcodigo","/recuperar-contrasena",
                		"/verificar-codigo", "/imagenes/vehiculos/**").permitAll()
                
                
                .requestMatchers("/reservar","/reservar/**","/cliente","/cliente/**","/detalle_valoracion","/detalle_valoracion/**","/miscitas","/reservar"
                		,"/reservar/**","/valorar","/valorar/**","/valoraciones").hasAnyRole("CLIENTE")
                
                
                .requestMatchers("/personal","editar","/editar/**","/editarvehiculos","/eliminarvehiculos","/gestiondecitas","/gestiondevehiculos",
                		"/personal","/personalpage").hasAnyRole("ADMIN", "PERSONAL") 
                .requestMatchers("/anadirPersonal","/editarpersonal","/editarpersonal/**","/eliminarpersonal","/gestiondepersonal","/listar","/listarclientes").hasAnyRole("ADMIN") 
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
                            response.sendRedirect("/personalpage");
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
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Usuario usuario = servusuario.findByUser(username);
                if (usuario == null) {
                    throw new UsernameNotFoundException("Usuario no encontrado: " + username);
                }
                return mapUsuarioToUserDetails(usuario);
            }
        };
    }

    private UserDetails mapUsuarioToUserDetails(Usuario usuario) {
       
        String role = (usuario.getCliente() != null) ? "ROLE_CLIENTE" : "ROLE_PERSONAL";
        if ("admin".equalsIgnoreCase(usuario.getUser())) {
            role = "ROLE_ADMIN";
        }

        Collection<GrantedAuthority> authorities = 
            java.util.Collections.singletonList(new SimpleGrantedAuthority(role));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUser())
                .password(usuario.getContraseña()) 
                .authorities(authorities)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}

