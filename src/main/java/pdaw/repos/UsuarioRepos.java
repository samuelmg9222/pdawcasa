package pdaw.repos;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pdaw.modelo.Usuario;

@Repository
public interface UsuarioRepos extends JpaRepository <Usuario,Long>{

	Usuario findByCorreo(String correo);
    Usuario findByCodigoRecuperacion(String codigoRecuperacion);
	Usuario findByUser(String user);
	  Page<Usuario> findByFechanacBetween(LocalDate desde, LocalDate hasta,Pageable pageable);
}
