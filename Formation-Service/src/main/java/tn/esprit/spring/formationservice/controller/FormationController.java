package tn.esprit.spring.formationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.dto.FormationRequest;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.services.interfaces.IFormationService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formation Management")
public class FormationController {

    private final IFormationService formationService;

    @Operation(summary = "Ajouter une formation avec image, pause et sponsor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Formation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur d'image ou données invalides")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Formation> createFormationWithImage( @RequestPart("request") FormationRequest request,
                                                                      @RequestPart("image") MultipartFile image) {
        try {
            Formation saved = formationService.addFormation(request, image);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Formation>> getAll() {
        return new ResponseEntity<>(formationService.getAllFormations(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formation> getById(@PathVariable Long id) {
        return formationService.getFormationById(id)
                .map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity<Formation> update(@RequestBody Formation formation) {
        return new ResponseEntity<>(formationService.updateFormation(formation), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        formationService.deleteFormation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/improve-description")
    public ResponseEntity<String> improveDescription(@RequestBody Map<String, String> body) {
        String input = body.get("text");
        if (input == null || input.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Texte requis");
        }
        // Appelle le service
        String improved = formationService.generateBetterDescription(input);
        return ResponseEntity.ok(improved);
    }


    @PostMapping("/suggest-sponsor")
    public ResponseEntity<Map<String, Object>> suggestSponsor(@RequestBody Map<String, Object> payload) {
        String description = (String) payload.get("description");
        Double prix = Double.parseDouble(payload.get("prix").toString());
        String mode = (String) payload.get("mode");
        String lieu = (String) payload.get("lieu");

        Optional<Sponsor> sponsorOpt = formationService.suggestSponsorForFormation(description);
        Map<String, Object> response = new HashMap<>();
        sponsorOpt.ifPresent(s -> response.put("sponsorId", s.getId()));
        return ResponseEntity.ok(response);
    }

}
