package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Logement;
import tn.esprit.spring.campingservice.Services.Interfaces.ILogementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Logement")
@RestController
@CrossOrigin("*")
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = logementService.uploadFile(file);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Image uploaded successfully");
        responseBody.put("fileUrl", fileUrl);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/byCentre/{centreId}")
    public List<Logement> getLogementsByCentre(@PathVariable Long centreId) {
        CentreCamping centre = new CentreCamping();
        centre.setIdCentre(centreId);
        return logementService.retrieveLogementsByCentre(centre);
    }
}
