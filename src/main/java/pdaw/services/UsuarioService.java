package pdaw.services;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pdaw.modelo.Cliente;
import pdaw.modelo.Usuario;
import pdaw.repos.UsuarioRepos;

@Service
public class UsuarioService {
	@Autowired
	UsuarioRepos usuariorepos;
	
	 public Page<Usuario> listarTodo(Pageable pageable) {
	        return usuariorepos.findAll(pageable);
	    }
	 public Page<Usuario> buscarPorFechaNacimientoEntre(LocalDate desde, LocalDate hasta, Pageable pageable) {
	        return usuariorepos.findByFechanacBetween(desde, hasta, pageable);
	    }
	    public Usuario buscarPorId(Long id) {
	        return usuariorepos.findById(id).orElse(null);
	    }
	public void insertarusuario(Usuario user) {
		usuariorepos.saveAndFlush(user);
	}
	public int exiteUser(String username) {
		List<Usuario> Todas =usuariorepos.findAll();
		for(Usuario c:Todas) {
			if(c.getUser().toLowerCase().equals(username.toLowerCase())){
				return 0;
			}
		}
		return 1;
	}
	public void eliminar(Usuario user) {
		usuariorepos.delete(user);
	}
	public int existeNEmail(String email) {
	    List<Usuario> todos = usuariorepos.findAll();
if(email==null) {
	return 0;
}
	    for (Usuario c : todos) {
	        if (c.getCorreo().equalsIgnoreCase(email)) {
	            return 1;  
	        }
	    }
	    return 0;  
	}
	public void insertarUser(Usuario user) {
		usuariorepos.saveAndFlush(user);
	}
	
	 public int findByUserOrEmail(String usernameOrEmail) {
	       List <Usuario> todos= usuariorepos.findAll();
	       for(Usuario t:todos) {
	    	   if(t.getCorreo().equalsIgnoreCase(usernameOrEmail)) return 1;
	    	   if(t.getUser().equalsIgnoreCase(usernameOrEmail)) return 1;
	    }
	       return 0;
	 }
	    public void saveCodigoRecuperacion(Usuario usuario, String codigo) {
	        usuario.setCodigoRecuperacion(codigo);
	        usuariorepos.save(usuario);
	    }

	    public Usuario findByCodigoRecuperacion(String codigoRecuperacion) {
	        return usuariorepos.findByCodigoRecuperacion(codigoRecuperacion);
	    }
	    
	    public Usuario findByCorreo(String correo) {
	        return usuariorepos.findByCorreo(correo);
	    }
	    public Usuario findByUser(String user) {
	        return usuariorepos.findByUser(user);
	    }
	   
	    public List<Usuario> findAll(){
	    	return usuariorepos.findAll();
	    }
	    public Usuario findByUsername(String user) {
	        return usuariorepos.findByUser(user);
	    }
}
