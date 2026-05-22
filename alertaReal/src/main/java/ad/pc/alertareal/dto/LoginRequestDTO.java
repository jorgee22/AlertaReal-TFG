package ad.pc.alertareal.dto;

import lombok.Data;

// DTO específico para recibir los datos del login desde Android
// Usa la misma estructura que el LoginRequestDTO.kt del móvil
@Data
public class LoginRequestDTO {
    private String username;
    private String pass;
}


