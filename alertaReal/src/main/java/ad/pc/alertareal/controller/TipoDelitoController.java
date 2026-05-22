package ad.pc.alertareal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ad.pc.alertareal.dto.TipoDelitoDTO;
import ad.pc.alertareal.service.TipoDelitoService;
import java.util.List;

@RestController
@RequestMapping("/alerta/tipos")
public class TipoDelitoController {
    @Autowired private TipoDelitoService service;

    @GetMapping
    public ResponseEntity<List<TipoDelitoDTO>> obtenerTodos() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TipoDelitoDTO> crear(@RequestBody TipoDelitoDTO dto) {
        dto.setId(null);
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }
}