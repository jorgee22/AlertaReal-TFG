package ad.pc.alertareal.service;

import ad.pc.alertareal.dto.AlertaDTO;
import ad.pc.alertareal.model.*;
import ad.pc.alertareal.repository.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class AlertaCompletaService {

    @Autowired private AlertaRepository alertaRepository;
    @Autowired private PoliciaRepository policiaRepository;
    @Autowired private TipoDelitoRepository tipoDelitoRepository;
    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private FabricaAlertaService fabrica;

    @Data
    public static class AlertaCompletaDTO {
        private Integer policiaId;
        private String nombreCalle;
        private String codigoPostal;
        private String ciudad;
        private Double latitud;
        private Double longitud;
        private Integer tipoDelitoId;
        private String descripcion;
        private Integer duracionEstimada;
        private String horaIncidente;
    }

    public AlertaDTO crearCompleta(AlertaCompletaDTO dto) {

        Policia policia = policiaRepository.findById(dto.getPoliciaId())
                .orElseThrow(() -> new NoSuchElementException("Policía no encontrado con id: " + dto.getPoliciaId()));

        TipoDelito tipoDelito = tipoDelitoRepository.findById(dto.getTipoDelitoId())
                .orElseThrow(() -> new NoSuchElementException("Tipo de delito no encontrado con id: " + dto.getTipoDelitoId()));

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombreCalle(dto.getNombreCalle());
        ubicacion.setCodigoPostal(dto.getCodigoPostal());
        ubicacion.setCiudad(dto.getCiudad());
        ubicacion.setLatitud(dto.getLatitud());
        ubicacion.setLongitud(dto.getLongitud());
        ubicacion = ubicacionRepository.save(ubicacion);

        Alerta alerta = new Alerta();
        alerta.setPolicia(policia);
        alerta.setTipoDelito(tipoDelito);
        alerta.setUbicacion(ubicacion);
        alerta.setDescripcion(dto.getDescripcion());
        alerta.setFechaHora(LocalDateTime.now());
        alerta.setDuracionEstimada(dto.getDuracionEstimada());
        alerta.setActiva(true);
        alerta.setHoraIncidente(dto.getHoraIncidente());

        return fabrica.createAlertaDTO(alertaRepository.save(alerta));
    }
}