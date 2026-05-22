package ad.pc.alertareal.repository;

import ad.pc.alertareal.model.Policia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PoliciaRepository extends JpaRepository<Policia, Integer> {
    Optional<Policia> findByNombreAndNumLicencia(String nombre, String numLicencia);
    Optional<Policia> findByNumLicencia(String numLicencia);
}