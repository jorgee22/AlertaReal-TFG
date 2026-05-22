package ad.pc.alertareal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ad.pc.alertareal.dto.AlertaDTO;
import ad.pc.alertareal.service.AlertaService;
import ad.pc.alertareal.service.AlertaCompletaService;
import java.util.List;

@RestController
@RequestMapping("/alerta/principal")
public class AlertaController {

    @Autowired
    private AlertaService service;

    @Autowired
    private AlertaCompletaService alertaCompletaService;

    // GET - alertas activas (no expiradas)
    @GetMapping("/activas")
    public ResponseEntity<List<AlertaDTO>> obtenerActivas() {
        return new ResponseEntity<>(service.findActivas(), HttpStatus.OK);
    }

    // POST - crear alerta con IDs ya existentes
    @PostMapping("/crear")
    public ResponseEntity<AlertaDTO> crear(@RequestBody AlertaDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    // POST - crear alerta completa en una sola llamada
    // No necesitas crear la ubicación ni el tipo por separado
    @PostMapping("/crear-completa")
    public ResponseEntity<AlertaDTO> crearCompleta(@RequestBody AlertaCompletaService.AlertaCompletaDTO dto) {
        return new ResponseEntity<>(alertaCompletaService.crearCompleta(dto), HttpStatus.CREATED);
    }
}   