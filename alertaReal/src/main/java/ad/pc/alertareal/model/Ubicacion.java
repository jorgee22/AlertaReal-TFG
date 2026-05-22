package ad.pc.alertareal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ad.pc.alertareal.dto.UbicacionDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ubicaciones") // Nombre físico en MariaDB
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID_Ubicacion (PK)

    private String nombreCalle; // Nombre_Calle
    private String codigoPostal; // Codigo_Postal
    private String ciudad; // Ciudad
    private Double latitud; // Latitud
    private Double longitud; // Longitud

    // Constructor para convertir DTO en Entidad (Estilo clase) [cite: 548]
    public Ubicacion(UbicacionDTO dto) {
        this.id = dto.getId();
        this.nombreCalle = dto.getNombreCalle();
        this.codigoPostal = dto.getCodigoPostal();
        this.ciudad = dto.getCiudad();
        this.latitud = dto.getLatitud();
        this.longitud = dto.getLongitud();
    }
}