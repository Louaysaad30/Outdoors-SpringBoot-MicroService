package tn.esprit.spring.campingservice.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.LigneReservation;
import tn.esprit.spring.campingservice.Services.Interfaces.ILigneReservationService;

import java.util.List;

@Tag(name = "LigneReservation")
@RestController
@AllArgsConstructor
@RequestMapping("/LigneReservation")
public class LigneReservationController {

    private ILigneReservationService ligneReservationService;

    @GetMapping("/all")
    public List<LigneReservation> getAllLigneReservations() {
        return ligneReservationService.retrieveAllLigneReservations();
    }

    @PostMapping("/add")
    public LigneReservation addLigneReservation(@RequestBody LigneReservation ligneReservation) {
        return ligneReservationService.addLigneReservation(ligneReservation);
    }

    @PutMapping("/update")
    public LigneReservation updateLigneReservation(@RequestBody LigneReservation ligneReservation) {
        return ligneReservationService.updateLigneReservation(ligneReservation);
    }

    @GetMapping("/get/{id}")
    public LigneReservation getLigneReservation(@PathVariable Long id) {
        return ligneReservationService.retrieveLigneReservation(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLigneReservation(@PathVariable Long id) {
        ligneReservationService.removeLigneReservation(id);
    }
}
