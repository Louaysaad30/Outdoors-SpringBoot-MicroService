package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.services.IMPL.VehiculeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @Autowired
    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public List<Vehicule> getAllVehicules() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    public Vehicule getVehiculeById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    public Vehicule createVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.save(vehicule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> updateVehicule(@PathVariable Long id, @RequestBody Vehicule vehiculeDetails) {
        Optional<Vehicule> optionalVehicule = Optional.ofNullable(vehiculeService.findById(id));
        if (optionalVehicule.isPresent()) {
            Vehicule vehicule = optionalVehicule.get();
            vehicule.setType(vehiculeDetails.getType());
            vehicule.setModele(vehiculeDetails.getModele());
            vehicule.setLocalisation(vehiculeDetails.getLocalisation());
            vehicule.setDisponible(vehiculeDetails.isDisponible());
            vehicule.setStatut(vehiculeDetails.getStatut());
            vehicule.setPrixParJour(vehiculeDetails.getPrixParJour());
            vehicule.setNbPlace(vehiculeDetails.getNbPlace());
            Vehicule updatedVehicule = vehiculeService.save(vehicule);
            return ResponseEntity.ok(updatedVehicule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public void deleteVehicule(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }
}
