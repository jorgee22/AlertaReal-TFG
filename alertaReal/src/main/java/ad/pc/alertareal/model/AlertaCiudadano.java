package ad.pc.alertareal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alertas_ciudadanos")
public class AlertaCiudadano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descripcion;
    private String nombreCalle;
    private String codigoPostal;
    private String ciudad;
    private Double latitud;
    private Double longitud;
    private String horaIncidente;
    private Integer tipoDelitoId;
    private String nombreCiudadano;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    // PENDIENTE, APROBADA, RECHAZADA
    private String estado;
}