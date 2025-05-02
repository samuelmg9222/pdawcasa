package pdaw.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pdaw.modelo.Cliente;

@Repository
public interface ClienteRepos extends JpaRepository <Cliente,Long> {



}
