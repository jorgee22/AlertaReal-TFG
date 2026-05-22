package ad.pc.alertareal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ad.pc.alertareal.dto.PoliciaDTO;
import ad.pc.alertareal.model.Policia;
import ad.pc.alertareal.repository.PoliciaRepository;
import java.util.List;

@Service
public class PoliciaService {
    @Autowired private FabricaPoliciaService fabrica;
    @Autowired private PoliciaRepository repository;

    public PoliciaDTO save(PoliciaDTO dto) {
        Policia entidad = fabrica.createPolicia(dto);
        return fabrica.createPoliciaDTO(repository.save(entidad));
    }

    public List<PoliciaDTO> findAll() {
        return fabrica.crearPoliciasDTO(repository.findAll());
    }
}