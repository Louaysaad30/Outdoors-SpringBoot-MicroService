package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Agence;
import tn.esprit.spring.transportservice.services.IMPL.AgenceService;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@CrossOrigin(origins = "http://localhost:4200")
public class AgenceController {

    private final AgenceService agenceService;

    @Autowired
    public AgenceController(AgenceService agenceService) {
        this.agenceService = agenceService;
    }


    @GetMapping
    public List<Agence> getAllAgences() {
        return agenceService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Agence> getAgenceById(@PathVariable Long id) {
        Agence agence = agenceService.findById(id);
        return agence != null ? ResponseEntity.ok(agence) : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Agence> createAgence(@RequestBody Agence agence) {
        Agence savedAgence = agenceService.save(agence);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAgence);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Agence> updateAgence(@PathVariable Long id, @RequestBody Agence agenceDetails) {
        Agence updatedAgence = agenceService.update(id, agenceDetails);
        return ResponseEntity.ok(updatedAgence);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgence(@PathVariable Long id) {
        agenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
