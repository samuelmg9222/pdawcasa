package pdaw.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pdaw.modelo.Usuario;

@Repository
public interface UsuarioRepos extends JpaRepository <Usuario,Long>{

	Usuario findByCorreo(String correo);
    Usuario findByCodigoRecuperacion(String codigoRecuperacion);
	Usuario findByUser(String user);
}
