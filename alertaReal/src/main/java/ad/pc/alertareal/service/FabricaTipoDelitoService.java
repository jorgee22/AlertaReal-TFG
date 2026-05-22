package ad.pc.alertareal.service;

import ad.pc.alertareal.dto.TipoDelitoDTO;
import ad.pc.alertareal.model.TipoDelito;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaTipoDelitoService {
    public TipoDelito createTipoDelito(TipoDelitoDTO dto) { return new TipoDelito(dto); }
    public TipoDelitoDTO createTipoDelitoDTO(TipoDelito entidad) { return new TipoDelitoDTO(entidad); }
    public List<TipoDelitoDTO> crearTiposDelitoDTO(List<TipoDelito> lista) {
        return lista.stream().map(this::createTipoDelitoDTO).collect(Collectors.toList());
    }
}