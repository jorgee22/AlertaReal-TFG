package ad.pc.alertareal.service;

import ad.pc.alertareal.dto.AlertaDTO;
import ad.pc.alertareal.model.Alerta;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaAlertaService {
    public Alerta createAlerta(AlertaDTO dto) { return new Alerta(dto); }
    public AlertaDTO createAlertaDTO(Alerta entidad) { return new AlertaDTO(entidad); }
    public List<AlertaDTO> crearAlertasDTO(List<Alerta> lista) {
        return lista.stream().map(this::createAlertaDTO).collect(Collectors.toList());
    }
}