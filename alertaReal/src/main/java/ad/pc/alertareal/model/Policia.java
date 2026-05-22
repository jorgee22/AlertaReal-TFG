package ad.pc.alertareal.model;

import jakarta.persistence.*;
import lombok.*;
import ad.pc.alertareal.dto.PoliciaDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="policias")
public class Policia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String numLicencia;

    private String cuerpoSeguridad;
    private String password;
    private String dni;

    public Policia(PoliciaDTO dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.numLicencia = dto.getNumLicencia();
        this.cuerpoSeguridad = dto.getCuerpoSeguridad();
        this.dni = dto.getDni();
    }
}