package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Materiel;
import tn.esprit.spring.campingservice.Services.Interfaces.IMaterielService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Materiel")
@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/Materiel")
public class MaterielController {

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

    @GetMapping("/byCentre/{centreId}")
    public List<Materiel> getMaterielsByCentre(@PathVariable Long centreId) {
        CentreCamping centre = new CentreCamping();
        centre.setIdCentre(centreId);
        return materielService.retrieveMaterielsByCentre(centre);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = materielService.uploadFile(file);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Image uploaded successfully");
        responseBody.put("fileUrl", fileUrl);

        return ResponseEntity.ok(responseBody);
    }
}
