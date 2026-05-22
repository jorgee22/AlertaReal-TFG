package ad.pc.alertareal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ad.pc.alertareal.model.Ubicacion;

@Data
@NoArgsConstructor
public class UbicacionDTO {

    private Integer id;
    private String nombreCalle;
    private String codigoPostal;
    private String ciudad;
    private Double latitud;
    private Double longitud;

    // Constructor para transformar Entidad en DTO (Estilo clase) [cite: 679]
    public UbicacionDTO(Ubicacion entidad) {
        this.id = entidad.getId();
        this.nombreCalle = entidad.getNombreCalle();
        this.codigoPostal = entidad.getCodigoPostal();
        this.ciudad = entidad.getCiudad();
        this.latitud = entidad.getLatitud();
        this.longitud = entidad.getLongitud();
    }
}