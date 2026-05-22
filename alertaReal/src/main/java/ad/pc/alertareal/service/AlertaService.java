package ad.pc.alertareal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ad.pc.alertareal.dto.AlertaDTO;
import ad.pc.alertareal.model.*;
import ad.pc.alertareal.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AlertaService {
    @Autowired private AlertaRepository alertaRepository;
    @Autowired private PoliciaRepository policiaRepository;
    @Autowired private TipoDelitoRepository tipoRepository;
    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private FabricaAlertaService fabrica;

    public AlertaDTO save(AlertaDTO dto) {
        Alerta alerta = fabrica.createAlerta(dto);

        alerta.setPolicia(policiaRepository.findById(dto.getPoliciaId())
                .orElseThrow(() -> new NoSuchElementException("Policía no encontrado")));
        alerta.setTipoDelito(tipoRepository.findById(dto.getTipoDelitoId())
                .orElseThrow(() -> new NoSuchElementException("Tipo de delito no válido")));
        alerta.setUbicacion(ubicacionRepository.findById(dto.getUbicacionId())
                .orElseThrow(() -> new NoSuchElementException("Ubicación no registrada")));

        if (alerta.getFechaHora() == null) {
            alerta.setFechaHora(LocalDateTime.now());
        }
        alerta.setActiva(true);

        return fabrica.createAlertaDTO(alertaRepository.save(alerta));
    }

    // Solo devuelve alertas vigentes: fecha_hora + duracion_estimada > ahora
    public List<AlertaDTO> findActivas() {
        LocalDateTime ahora = LocalDateTime.now();
        return fabrica.crearAlertasDTO(
                alertaRepository.findAll().stream()
                        .filter(a -> a.getActiva() != null && a.getActiva())
                        .filter(a -> {
                            if (a.getDuracionEstimada() == null) return true;
                            LocalDateTime expira = a.getFechaHora().plusMinutes(a.getDuracionEstimada());
                            return ahora.isBefore(expira);
                        })
                        .toList()
        );
    }
}