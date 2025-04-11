package tn.esprit.spring.transportservice.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Agence;
import tn.esprit.spring.transportservice.entity.Vehicule;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.transportservice.enums.StatutVehicule;
import tn.esprit.spring.transportservice.enums.TypeVehicule;
import tn.esprit.spring.transportservice.repository.AgenceRepository;
import tn.esprit.spring.transportservice.services.interfaces.IVehiculeService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/vehicules")
@CrossOrigin(origins = "http://localhost:4200")
public class VehiculeController {
    @Autowired
    private Cloudinary cloudinary;

    private final IVehiculeService vehiculeService;


    @Autowired
    private AgenceRepository agenceRepository;

    @Autowired
    public VehiculeController(IVehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public List<Vehicule> getAllVehicules() {
        return vehiculeService.findAll();
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Vehicule> addVehiculeWithImage(
            @RequestParam("modele") String modele,
            @RequestParam("disponible") boolean disponible,
            @RequestParam("statut") String statut,
            @RequestParam("localisation") String localisation,
            @RequestParam("prixParJour") Double prixParJour,
            @RequestParam("nbPlace") Integer nbPlace,
            @RequestParam("rating") Double rating,
            @RequestParam("agenceId") Long agenceId,
            @RequestParam("type") String type,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {

            TypeVehicule vehiculeType = TypeVehicule.valueOf(type.toUpperCase());

            Optional<Agence> agenceOpt = agenceRepository.findById(agenceId);
                if (agenceOpt.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

            Agence agence = agenceOpt.get();

            Vehicule vehicule = Vehicule.builder()
                    .modele(modele)
                    .disponible(disponible)
                    .statut(StatutVehicule.valueOf(statut))
                    .localisation(localisation)
                    .prixParJour(prixParJour)
                    .nbPlace(nbPlace)
                    .rating(rating)
                    .agence(agence)
                    .type(vehiculeType)
                    .build();

            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                vehicule.setImage(imageUrl);
            }

            Vehicule savedVehicule = vehiculeService.addVehiculeWithImage(vehicule, imageFile);
            return new ResponseEntity<>(savedVehicule, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> getVehiculeById(@PathVariable Long id) {
        Vehicule vehicule = vehiculeService.findById(id);
        return ResponseEntity.ok(vehicule);
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
