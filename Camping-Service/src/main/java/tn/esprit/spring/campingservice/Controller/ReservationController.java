package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.Materiel;
import tn.esprit.spring.campingservice.Services.Interfaces.IMaterielService;

import java.util.List;

@Tag(name = "Reservation")
@RestController
@AllArgsConstructor
@RequestMapping("/Reservation")
public class ReservationController {

    private IMaterielService materielService;

    @GetMapping("/all")
    public List<Materiel> getAllMateriels() {
        return materielService.retrieveAllMateriels();
    }

    @PostMapping("/add")
    public Materiel addMateriel(@RequestBody Materiel materiel) {
        return materielService.addMateriel(materiel);
    }

    @PutMapping("/update")
    public Materiel updateMateriel(@RequestBody Materiel materiel) {
        return materielService.updateMateriel(materiel);
    }

    @GetMapping("/get/{id}")
    public Materiel getMateriel(@PathVariable Long id) {
        return materielService.retrieveMateriel(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMateriel(@PathVariable Long id) {
        materielService.removeMateriel(id);
    }
}
