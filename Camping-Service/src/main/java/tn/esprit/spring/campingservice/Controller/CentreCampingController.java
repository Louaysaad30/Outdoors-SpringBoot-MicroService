package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Services.Interfaces.ICentreCampingService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "CentreCamping")
@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/CentreCamping")
public class CentreCampingController {

    private ICentreCampingService centreCampingService;

    @GetMapping("/all")
    public List<CentreCamping> getAllCentreCamping() {
        return centreCampingService.retrieveAllCentreCamping();
    }

    @PostMapping("/add")
    public CentreCamping addCentreCamping(@RequestBody CentreCamping centreCamping) {
        return centreCampingService.addCentreCamping(centreCamping);
    }

    @PutMapping("/update/{id}")
    public CentreCamping updateCentreCamping(@PathVariable Long id, @RequestBody CentreCamping centreCamping) {
        return centreCampingService.updateCentreCamping(id, centreCamping);
    }
    @GetMapping("/get/{id}")
    public CentreCamping getCentreCamping(@PathVariable Long id) {
        return centreCampingService.retrieveCentreCamping(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCentreCamping(@PathVariable Long id) {
        centreCampingService.removeCentreCamping(id);
    }


    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String fileUrl = centreCampingService.uploadFile(file);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Profile picture updated successfully");
        responseBody.put("fileUrl", fileUrl);


        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/verify/{id}")
    public CentreCamping verifyCentreCamping(@PathVariable Long id) {
        return centreCampingService.verifyCentreCamping(id);
    }

    @GetMapping("/my/{idOwner}")
    public List<CentreCamping> getCentreCampingByOwner(@PathVariable Long idOwner) {
        return centreCampingService.retrieveCentreCampingByOwner(idOwner);
    }

    @GetMapping("/verified")
    public List<CentreCamping> getVerifiedCentreCamping() {
        return centreCampingService.retrieveVerifiedCentreCamping();
    }

    @PutMapping("/deactivate/{id}")
    public CentreCamping deactivateCentreCamping(@PathVariable Long id) {
        return centreCampingService.deactivateCentreCamping(id);
    }
}
