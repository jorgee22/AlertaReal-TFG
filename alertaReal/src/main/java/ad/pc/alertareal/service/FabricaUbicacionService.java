package ad.pc.alertareal.service;

import ad.pc.alertareal.dto.UbicacionDTO;
import ad.pc.alertareal.model.Ubicacion;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaUbicacionService {

    public Ubicacion createUbicacion(UbicacionDTO dto) {
        Ubicacion entidad = new Ubicacion();
        entidad.setId(dto.getId());
        entidad.setNombreCalle(dto.getNombreCalle());
        entidad.setCodigoPostal(dto.getCodigoPostal());
        entidad.setCiudad(dto.getCiudad());
        entidad.setLatitud(dto.getLatitud());
        entidad.setLongitud(dto.getLongitud());
        return entidad;
    }

    public UbicacionDTO createUbicacionDTO(Ubicacion entidad) {
        return new UbicacionDTO(entidad);
    }

    public List<UbicacionDTO> crearUbicacionesDTO(List<Ubicacion> lista) {
        return lista.stream().map(this::createUbicacionDTO).collect(Collectors.toList());
    }
}