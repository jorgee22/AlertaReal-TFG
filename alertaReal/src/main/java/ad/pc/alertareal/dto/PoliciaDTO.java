package ad.pc.alertareal.dto;

import ad.pc.alertareal.model.Policia;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PoliciaDTO {
    private Integer id;
    private String nombre;
    private String numLicencia;
    private String cuerpoSeguridad;
    private String dni;

    public PoliciaDTO(Policia policia) {
        this.id = policia.getId();
        this.nombre = policia.getNombre();
        this.numLicencia = policia.getNumLicencia();
        this.cuerpoSeguridad = policia.getCuerpoSeguridad();
        this.dni = policia.getDni();
    }
}