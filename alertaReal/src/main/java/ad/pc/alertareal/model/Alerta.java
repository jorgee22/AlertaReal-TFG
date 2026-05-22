package ad.pc.alertareal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ad.pc.alertareal.dto.AlertaDTO;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private Integer duracionEstimada;

    private Boolean activa;

    @Column(name = "hora_incidente")
    private String horaIncidente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente", nullable = false)
    private Policia policia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoDelito tipoDelito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    public Alerta(AlertaDTO dto) {
        this.id = dto.getId();
        this.descripcion = dto.getDescripcion();
        this.fechaHora = dto.getFechaHora() != null ? dto.getFechaHora() : LocalDateTime.now();
        this.duracionEstimada = dto.getDuracionEstimada();
        this.activa = dto.getActiva() != null ? dto.getActiva() : true;
        this.horaIncidente = dto.getHoraIncidente();
    }
}