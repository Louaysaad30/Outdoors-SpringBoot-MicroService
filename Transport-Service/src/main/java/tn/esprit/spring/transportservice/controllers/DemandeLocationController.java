package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.DemandeLocation;
import tn.esprit.spring.transportservice.services.IMPL.DemandeLocationService;
import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "http://localhost:4200")
public class DemandeLocationController {

    private final DemandeLocationService demandeLocationService;

    @Autowired
    public DemandeLocationController(DemandeLocationService demandeLocationService) {
        this.demandeLocationService = demandeLocationService;
    }

    // Get all demandes
    @GetMapping
    public List<DemandeLocation> getAllDemandes() {
        return demandeLocationService.findAll();
    }

    //Get by id user
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<DemandeLocation>> getDemandesByUserId(@PathVariable Long userId) {
        List<DemandeLocation> demandes = demandeLocationService.getDemandesByUserId(userId);
        return ResponseEntity.ok(demandes);
    }

    // Get demande by ID
    @GetMapping("/{id}")
    public ResponseEntity<DemandeLocation> getDemandeById(@PathVariable Long id) {
        DemandeLocation demandeLocation = demandeLocationService.findById(id);
        return demandeLocation != null ? ResponseEntity.ok(demandeLocation) : ResponseEntity.notFound().build();
    }

    // Create new demande
    @PostMapping
    public ResponseEntity<DemandeLocation> createDemande(@RequestBody DemandeLocation demandeLocation) {
        DemandeLocation savedDemande = demandeLocationService.save(demandeLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDemande);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DemandeLocation> updateDemande(@PathVariable Long id, @RequestBody DemandeLocation demandeLocationDetails) {
        try {
            DemandeLocation updatedDemande = demandeLocationService.update(id, demandeLocationDetails);
            return ResponseEntity.ok(updatedDemande);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        try {
            demandeLocationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}/statut")
    public ResponseEntity<DemandeLocation> updateStatut(@PathVariable Long id, @RequestParam DemandeLocation.StatutDemande statut) {
        try {
            DemandeLocation updated = demandeLocationService.updateStatut(id, statut);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/{id}/rejeter")
    public ResponseEntity<DemandeLocation> rejectDemande(
            @PathVariable Long id,
            @RequestParam String cause) {
        try {
            DemandeLocation rejected = demandeLocationService.rejectDemande(id, cause);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Obtenir les demandes d’une agence
    @GetMapping("/by-agence/{agenceId}")
    public ResponseEntity<List<DemandeLocation>> getDemandesByAgence(@PathVariable Long agenceId) {
        List<DemandeLocation> demandes = demandeLocationService.getDemandesByAgence(agenceId);
        return ResponseEntity.ok(demandes);
    }



}
