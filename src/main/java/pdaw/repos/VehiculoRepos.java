package pdaw.repos;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pdaw.modelo.Vehiculo;


@Repository
public interface VehiculoRepos extends JpaRepository <Vehiculo,Long>{
	 Page<Vehiculo> findAll(Pageable pageable);
}
