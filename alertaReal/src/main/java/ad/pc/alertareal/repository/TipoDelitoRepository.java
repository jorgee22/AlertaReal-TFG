package ad.pc.alertareal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ad.pc.alertareal.model.TipoDelito;

@Repository
public interface TipoDelitoRepository extends JpaRepository<TipoDelito, Integer> {
}