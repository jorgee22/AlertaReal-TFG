package ad.pc.alertareal.service;

import ad.pc.alertareal.dto.PoliciaDTO;
import ad.pc.alertareal.model.Policia;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaPoliciaService {
    public Policia createPolicia(PoliciaDTO dto) { return new Policia(dto); }
    public PoliciaDTO createPoliciaDTO(Policia entidad) { return new PoliciaDTO(entidad); }
    public List<PoliciaDTO> crearPoliciasDTO(List<Policia> lista) {
        return lista.stream().map(this::createPoliciaDTO).collect(Collectors.toList());
    }
}