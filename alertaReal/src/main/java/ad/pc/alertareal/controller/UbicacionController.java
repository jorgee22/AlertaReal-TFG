package ad.pc.alertareal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ad.pc.alertareal.dto.UbicacionDTO;
import ad.pc.alertareal.service.UbicacionService;
import java.util.List;

@RestController
@RequestMapping("/alerta/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> obtenerTodos() {
        return new ResponseEntity<>(ubicacionService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UbicacionDTO> crear(@RequestBody UbicacionDTO dto) {
        dto.setId(null);
        return new ResponseEntity<>(ubicacionService.save(dto), HttpStatus.CREATED);
    }
}