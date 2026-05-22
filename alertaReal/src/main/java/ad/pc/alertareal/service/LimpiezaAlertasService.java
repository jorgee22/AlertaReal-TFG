package ad.pc.alertareal.service;

import ad.pc.alertareal.repository.AlertaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LimpiezaAlertasService {

    @Autowired
    private AlertaRepository alertaRepository;

    // Se ejecuta cada 6 horas automáticamente
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    @Transactional
    public void desactivarAlertasAntiguas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(6);
        int desactivadas = alertaRepository.desactivarAlertasAntiguas(limite);
        System.out.println("Alertas desactivadas automáticamente: " + desactivadas);
    }
}