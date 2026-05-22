package ad.pc.alertareal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ad.pc.alertareal.dto.PoliciaDTO;
import ad.pc.alertareal.model.Policia;
import ad.pc.alertareal.repository.PoliciaRepository;
import ad.pc.alertareal.service.PoliciaService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alerta/policias")
public class PoliciaController {

    @Autowired private PoliciaRepository policiaRepository;
    @Autowired private PoliciaService service;

    @GetMapping
    public ResponseEntity<List<PoliciaDTO>> obtenerTodos() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, String> datos) {
        String nombre = datos.get("nombre");
        String numLicencia = datos.get("numLicencia");
        String cuerpoSeguridad = datos.get("cuerpoSeguridad");
        String dni = datos.get("dni");

        // Validar que no existe ya ese número de licencia
        if (policiaRepository.findByNumLicencia(numLicencia).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ese número de licencia ya está registrado"));
        }

        // Validar formato DNI español
        if (!validarDni(dni)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El DNI introducido no es válido"));
        }

        Policia policia = new Policia();
        policia.setNombre(nombre);
        policia.setNumLicencia(numLicencia);
        policia.setCuerpoSeguridad(cuerpoSeguridad);
        policia.setDni(dni.toUpperCase());
        policiaRepository.save(policia);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Policía registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        String nombre = datos.get("nombre");
        String numLicencia = datos.get("numLicencia");

        Optional<Policia> policiaOpt = policiaRepository.findByNombreAndNumLicencia(nombre, numLicencia);

        if (policiaOpt.isPresent()) {
            Policia policia = policiaOpt.get();
            return ResponseEntity.ok(Map.of(
                    "id", policia.getId(),
                    "nombre", policia.getNombre(),
                    "cuerpoSeguridad", policia.getCuerpoSeguridad(),
                    "rol", "POLICIA"
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Nombre o número de licencia incorrectos"));
    }

    // Algoritmo oficial de validación del DNI español
    private boolean validarDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}[A-Za-z]")) return false;
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraCorrecta = letras.charAt(numero % 23);
        char letraIntroducida = Character.toUpperCase(dni.charAt(8));
        return letraCorrecta == letraIntroducida;
    }
}