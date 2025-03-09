package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Services.Interfaces.ICentreCampingService;

import java.util.List;

@Tag(name = "CentreCamping")
@RestController
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

    @PutMapping("/update")
    public CentreCamping updateCentreCamping(@RequestBody CentreCamping centreCamping) {
        return centreCampingService.updateCentreCamping(centreCamping);
    }

    @GetMapping("/get/{id}")
    public CentreCamping getCentreCamping(@PathVariable Long id) {
        return centreCampingService.retrieveCentreCamping(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCentreCamping(@PathVariable Long id) {
        centreCampingService.removeCentreCamping(id);
    }
}
