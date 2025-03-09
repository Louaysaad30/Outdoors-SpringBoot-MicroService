package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.Logement;
import tn.esprit.spring.campingservice.Services.Interfaces.ILogementService;

import java.util.List;

@Tag(name = "Logement")
@RestController
@AllArgsConstructor
@RequestMapping("/Logement")
public class LogementController {

    private ILogementService logementService;

    @GetMapping("/all")
    public List<Logement> getAllLogements() {
        return logementService.retrieveAllLogements();
    }

    @PostMapping("/add")
    public Logement addLogement(@RequestBody Logement logement) {
        return logementService.addLogement(logement);
    }

    @PutMapping("/update")
    public Logement updateLogement(@RequestBody Logement logement) {
        return logementService.updateLogement(logement);
    }

    @GetMapping("/get/{id}")
    public Logement getLogement(@PathVariable Long id) {
        return logementService.retrieveLogement(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLogement(@PathVariable Long id) {
        logementService.removeLogement(id);
    }
}
