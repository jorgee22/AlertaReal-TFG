package ad.pc.alertareal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ad.pc.alertareal.dto.UbicacionDTO;
import ad.pc.alertareal.repository.UbicacionRepository;
import java.util.List;

@Service
public class UbicacionService {
    @Autowired private FabricaUbicacionService fabricaUbicacionService;
    @Autowired private UbicacionRepository ubicacionRepository;

    public UbicacionDTO save(UbicacionDTO dto) {
        // Convertimos el DTO a Entidad, guardamos en DB y volvemos a DTO
        return fabricaUbicacionService.createUbicacionDTO(
                ubicacionRepository.save(fabricaUbicacionService.createUbicacion(dto))
        );
    }

    public List<UbicacionDTO> findAll() {
        return fabricaUbicacionService.crearUbicacionesDTO(ubicacionRepository.findAll());
    }
}