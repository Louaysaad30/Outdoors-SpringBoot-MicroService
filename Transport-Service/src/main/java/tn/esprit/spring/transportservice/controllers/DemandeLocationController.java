package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.DemandeLocation;
import tn.esprit.spring.transportservice.services.IMPL.DemandeLocationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/demandes")
public class DemandeLocationController {

    private final DemandeLocationService demandeLocationService;

    @Autowired
    public DemandeLocationController(DemandeLocationService demandeLocationService) {
        this.demandeLocationService = demandeLocationService;
    }

    @GetMapping
    public List<DemandeLocation> getAllDemandes() {
        return demandeLocationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeLocation> getDemandeById(@PathVariable Long id) {
        Optional<DemandeLocation> demandeLocation = Optional.ofNullable(demandeLocationService.findById(id));
        return demandeLocation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DemandeLocation> createDemande(@RequestBody DemandeLocation demandeLocation) {
        demandeLocation.setDebutLocation(demandeLocation.getDebutLocation());
        demandeLocation.setFinLocation(demandeLocation.getFinLocation());
        demandeLocation.setPrixTotal(demandeLocation.getPrixTotal());
        demandeLocation.setStatut(DemandeLocation.StatutDemande.EN_ATTENTE);
        DemandeLocation savedDemande = demandeLocationService.save(demandeLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDemande);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DemandeLocation> updateDemande(@PathVariable Long id, @RequestBody DemandeLocation demandeDetails) {
        Optional<DemandeLocation> existingDemande = Optional.ofNullable(demandeLocationService.findById(id));
        if (existingDemande.isPresent()) {
            DemandeLocation demandeToUpdate = existingDemande.get();
            demandeToUpdate.setVehicule(demandeDetails.getVehicule());
            demandeToUpdate.setDebutLocation(demandeDetails.getDebutLocation());
            demandeToUpdate.setFinLocation(demandeDetails.getFinLocation());
            demandeToUpdate.setPrixTotal(demandeDetails.getPrixTotal());
            demandeToUpdate.setStatut(demandeDetails.getStatut());
            DemandeLocation updatedDemande = demandeLocationService.save(demandeToUpdate);
            return ResponseEntity.ok(updatedDemande);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        Optional<DemandeLocation> existingDemande = Optional.ofNullable(demandeLocationService.findById(id));
        if (existingDemande.isPresent()) {
            demandeLocationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}