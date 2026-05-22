package ad.pc.alertareal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ad.pc.alertareal.model.Alerta;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    List<Alerta> findByActivaTrue();

    @Modifying
    @Query("UPDATE Alerta a SET a.activa = false WHERE a.activa = true AND a.fechaHora < :limite")
    int desactivarAlertasAntiguas(LocalDateTime limite);
}