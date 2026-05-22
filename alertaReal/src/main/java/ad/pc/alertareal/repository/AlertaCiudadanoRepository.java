package ad.pc.alertareal.repository;

import ad.pc.alertareal.model.AlertaCiudadano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaCiudadanoRepository extends JpaRepository<AlertaCiudadano, Integer> {
    List<AlertaCiudadano> findByEstado(String estado);
}