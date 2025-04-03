package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.formationservice.entity.Pause;
import tn.esprit.spring.formationservice.services.interfaces.IPauseService;

import java.util.List;

@RestController
@RequestMapping("/api/pauses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PauseController {

    private final IPauseService pauseService;

    @PostMapping
    public ResponseEntity<Pause> add(@RequestBody Pause pause) {
        return new ResponseEntity<>(pauseService.addPause(pause), HttpStatus.CREATED);
    }

    @GetMapping("/formation/{id}")
    public ResponseEntity<List<Pause>> getByFormation(@PathVariable Long id) {
        return new ResponseEntity<>(pauseService.getPausesByFormation(id), HttpStatus.OK);
    }
}
