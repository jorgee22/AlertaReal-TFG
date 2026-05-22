package ad.pc.alertareal.dto;

import ad.pc.alertareal.model.Alerta;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AlertaDTO {

    private Integer id;
    private String descripcion;
    private LocalDateTime fechaHora;
    private Integer duracionEstimada;
    private Boolean activa;
    private String horaIncidente;

    private Integer policiaId;
    private Integer tipoDelitoId;
    private Integer ubicacionId;

    private Double latitud;
    private Double longitud;
    private Integer nivelAlerta;
    private String tipoDelito;
    private String ciudad;
    private String nombreCalle;
    private String codigoPostal;

    public AlertaDTO(Alerta alerta) {
        this.id = alerta.getId();
        this.descripcion = alerta.getDescripcion();
        this.fechaHora = alerta.getFechaHora();
        this.duracionEstimada = alerta.getDuracionEstimada();
        this.activa = alerta.getActiva();
        this.horaIncidente = alerta.getHoraIncidente();

        if (alerta.getPolicia() != null) {
            this.policiaId = alerta.getPolicia().getId();
        }
        if (alerta.getTipoDelito() != null) {
            this.tipoDelitoId = alerta.getTipoDelito().getId();
            this.nivelAlerta = alerta.getTipoDelito().getNivelAlerta();
            this.tipoDelito = alerta.getTipoDelito().getNombre();
        }
        if (alerta.getUbicacion() != null) {
            this.ubicacionId = alerta.getUbicacion().getId();
            this.latitud = alerta.getUbicacion().getLatitud();
            this.longitud = alerta.getUbicacion().getLongitud();
            this.ciudad = alerta.getUbicacion().getCiudad();
            this.nombreCalle = alerta.getUbicacion().getNombreCalle();
            this.codigoPostal = alerta.getUbicacion().getCodigoPostal();
        }
    }
}