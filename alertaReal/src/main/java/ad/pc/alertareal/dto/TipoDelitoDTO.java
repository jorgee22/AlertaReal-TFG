package ad.pc.alertareal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ad.pc.alertareal.model.TipoDelito;

@Data
@NoArgsConstructor
public class TipoDelitoDTO {
    private Integer id;
    private String nombre;
    private Integer nivelAlerta;

    public TipoDelitoDTO(TipoDelito entidad) {
        this.id = entidad.getId();
        this.nombre = entidad.getNombre();
        this.nivelAlerta = entidad.getNivelAlerta();
    }
}