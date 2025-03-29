package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Agence;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.services.IMPL.AgenceService;
import tn.esprit.spring.transportservice.services.IMPL.VehiculeService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/agences")
public class AgenceController {

    private final AgenceService agenceService;

    @Autowired
    private VehiculeService vehiculeService;


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
        Optional<Agence> agence = Optional.ofNullable(agenceService.findById(id));
        return agence.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Agence> createAgence(@RequestBody Agence agence) {
        if (agence.getVehicules() != null) {
            for (Vehicule vehicule : agence.getVehicules()) {
                vehicule.setAgence(agence);  // Set the agence for each vehicule
                vehiculeService.save(vehicule);  // Save the vehicule to persist the relationship
            }
        }

        Agence savedAgence = agenceService.save(agence);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAgence);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Agence> updateAgence(@PathVariable Long id, @RequestBody Agence agenceDetails) {
        Optional<Agence> existingAgence = Optional.ofNullable(agenceService.findById(id));
        if (existingAgence.isPresent()) {
            Agence agenceToUpdate = existingAgence.get();
            agenceToUpdate.setNom(agenceDetails.getNom());
            agenceToUpdate.setAdresse(agenceDetails.getAdresse());

            Agence updatedAgence = agenceService.save(agenceToUpdate);
            return ResponseEntity.ok(updatedAgence);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgence(@PathVariable Long id) {
        Optional<Agence> existingAgence = Optional.ofNullable(agenceService.findById(id));
        if (existingAgence.isPresent()) {
            agenceService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}