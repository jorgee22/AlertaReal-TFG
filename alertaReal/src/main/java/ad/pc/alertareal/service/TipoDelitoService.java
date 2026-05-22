package ad.pc.alertareal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ad.pc.alertareal.dto.TipoDelitoDTO;
import ad.pc.alertareal.model.TipoDelito;
import ad.pc.alertareal.repository.TipoDelitoRepository;
import java.util.List;

@Service
public class TipoDelitoService {
    @Autowired private FabricaTipoDelitoService fabrica;
    @Autowired private TipoDelitoRepository repository;

    public TipoDelitoDTO save(TipoDelitoDTO dto) {
        TipoDelito entidad = fabrica.createTipoDelito(dto);
        return fabrica.createTipoDelitoDTO(repository.save(entidad));
    }

    public List<TipoDelitoDTO> findAll() {
        return fabrica.crearTiposDelitoDTO(repository.findAll());
    }
}