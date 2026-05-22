package ad.pc.alertareal.model;

import jakarta.persistence.*;
import lombok.*;
import ad.pc.alertareal.dto.TipoDelitoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tipos_delito")
public class TipoDelito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre; // Ej: Robo con violencia, Agresión...

    private Integer nivelAlerta; // Gravedad del 1 al 3

    public TipoDelito(TipoDelitoDTO dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.nivelAlerta = dto.getNivelAlerta();
    }
}