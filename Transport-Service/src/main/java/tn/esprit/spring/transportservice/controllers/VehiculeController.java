package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.services.IMPL.VehiculeService;
import tn.esprit.spring.transportservice.services.interfaces.IVehiculeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicules")
@CrossOrigin(origins = "http://localhost:4200")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Autowired
    public VehiculeController(IVehiculeService vehiculeService) {
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
    public Vehicule updateVehicule(@PathVariable Long id, @RequestBody Vehicule updatedVehicule) {
        return vehiculeService.update(id, updatedVehicule);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicule(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }
}
