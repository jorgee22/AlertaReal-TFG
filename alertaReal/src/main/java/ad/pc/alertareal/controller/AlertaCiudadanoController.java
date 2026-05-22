package ad.pc.alertareal.controller;

import ad.pc.alertareal.model.AlertaCiudadano;
import ad.pc.alertareal.model.Ubicacion;
import ad.pc.alertareal.model.TipoDelito;
import ad.pc.alertareal.model.Policia;
import ad.pc.alertareal.model.Alerta;
import ad.pc.alertareal.repository.*;
import ad.pc.alertareal.service.FabricaAlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alerta/ciudadanos")
public class AlertaCiudadanoController {

    @Autowired private AlertaCiudadanoRepository alertaCiudadanoRepository;
    @Autowired private AlertaRepository alertaRepository;
    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private TipoDelitoRepository tipoDelitoRepository;
    @Autowired private PoliciaRepository policiaRepository;
    @Autowired private FabricaAlertaService fabrica;

    @PostMapping("/reportar")
    public ResponseEntity<?> reportar(@RequestBody Map<String, Object> datos) {
        AlertaCiudadano alerta = new AlertaCiudadano();
        alerta.setNombreCiudadano(datos.get("nombreCiudadano").toString());
        alerta.setDescripcion(datos.get("descripcion").toString());
        alerta.setNombreCalle(datos.get("nombreCalle").toString());
        alerta.setCodigoPostal(datos.get("codigoPostal").toString());
        alerta.setCiudad(datos.get("ciudad").toString());
        alerta.setLatitud(Double.parseDouble(datos.get("latitud").toString()));
        alerta.setLongitud(Double.parseDouble(datos.get("longitud").toString()));
        alerta.setHoraIncidente(datos.get("horaIncidente").toString());
        alerta.setTipoDelitoId(Integer.parseInt(datos.get("tipoDelitoId").toString()));
        alerta.setFechaHora(LocalDateTime.now());
        alerta.setEstado("PENDIENTE");
        alertaCiudadanoRepository.save(alerta);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Alerta reportada. Pendiente de aprobación."));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<AlertaCiudadano>> getPendientes() {
        return ResponseEntity.ok(alertaCiudadanoRepository.findByEstado("PENDIENTE"));
    }

    @PostMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobar(@PathVariable Integer id,
                                     @RequestBody Map<String, Object> datos) {
        try {
            Optional<AlertaCiudadano> opt = alertaCiudadanoRepository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            AlertaCiudadano ac = opt.get();
            ac.setEstado("APROBADA");
            alertaCiudadanoRepository.save(ac);

            // Convertir policiaId correctamente (Gson lo envía como Double)
            Object policiaIdObj = datos.get("policiaId");
            Integer policiaId;
            if (policiaIdObj instanceof Double) {
                policiaId = ((Double) policiaIdObj).intValue();
            } else if (policiaIdObj instanceof Integer) {
                policiaId = (Integer) policiaIdObj;
            } else {
                policiaId = Integer.parseInt(policiaIdObj.toString());
            }

            System.out.println("Aprobando alerta " + id + " con policiaId=" + policiaId);

            Policia policia = policiaRepository.findById(policiaId)
                    .orElseThrow(() -> new RuntimeException("Policía no encontrado con id: " + policiaId));
            TipoDelito tipoDelito = tipoDelitoRepository.findById(ac.getTipoDelitoId())
                    .orElseThrow(() -> new RuntimeException("Tipo de delito no encontrado"));

            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setNombreCalle(ac.getNombreCalle());
            ubicacion.setCodigoPostal(ac.getCodigoPostal());
            ubicacion.setCiudad(ac.getCiudad());
            ubicacion.setLatitud(ac.getLatitud());
            ubicacion.setLongitud(ac.getLongitud());
            ubicacion = ubicacionRepository.save(ubicacion);

            Alerta alerta = new Alerta();
            alerta.setPolicia(policia);
            alerta.setTipoDelito(tipoDelito);
            alerta.setUbicacion(ubicacion);
            alerta.setDescripcion(ac.getDescripcion());
            alerta.setFechaHora(LocalDateTime.now());
            alerta.setDuracionEstimada(60);
            alerta.setActiva(true);
            alerta.setHoraIncidente(ac.getHoraIncidente());
            alertaRepository.save(alerta);

            return ResponseEntity.ok(Map.of("mensaje", "Alerta aprobada y publicada en el mapa"));

        } catch (Exception e) {
            System.err.println("Error al aprobar alerta: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/rechazar/{id}")
    public ResponseEntity<?> rechazar(@PathVariable Integer id) {
        Optional<AlertaCiudadano> opt = alertaCiudadanoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AlertaCiudadano ac = opt.get();
        ac.setEstado("RECHAZADA");
        alertaCiudadanoRepository.save(ac);
        return ResponseEntity.ok(Map.of("mensaje", "Alerta rechazada"));
    }
}