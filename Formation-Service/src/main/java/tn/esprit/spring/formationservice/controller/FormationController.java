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
import tn.esprit.spring.formationservice.dto.FormationResponseDTO;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.repository.CategorieRepository;
import tn.esprit.spring.formationservice.repository.SponsorRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.formationservice.services.interfaces.IFormationService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formation Management")
public class FormationController {

    private final CategorieRepository categorieRepository;
    private final IFormationService formationService;
    private final SponsorRepository sponsorRepository;
    private final ICloudinaryService cloudinaryService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    // 📌 Attend des dates sous forme 2025-04-09T17:29

    @Operation(summary = "Ajouter une formation avec image, pause et sponsor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Formation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur d'image ou données invalides")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Formation> createFormationWithImage(
            @RequestPart("request") FormationRequest request,
            @RequestPart("image") MultipartFile image) {
        try {
            Formation saved = formationService.addFormation(request, image);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<FormationResponseDTO>> getAll() {
        List<Formation> formations = formationService.getAllFormations();

        List<FormationResponseDTO> dtos = formations.stream().map(f -> {
            Map<String, Object> formateurData = formationService.getFormateurData(f.getFormateurId());

            return FormationResponseDTO.builder()
                    .id(f.getId())
                    .titre(f.getTitre())
                    .description(f.getDescription())
                    .prix(f.getPrix())
                    .imageUrl(f.getImageUrl())
                    .enLigne(f.isEnLigne())
                    .lieu(f.getLieu())
                    .meetLink(f.getMeetLink())
                    .dateDebut(f.getDateDebut())
                    .dateFin(f.getDateFin())
                    .datePublication(f.getDatePublication())
                    .formateurId(f.getFormateurId())
                    .formateurNom((String) formateurData.getOrDefault("nom", ""))
                    .formateurPrenom((String) formateurData.getOrDefault("prenom", ""))
                    .formateurImage((String) formateurData.getOrDefault("image", "assets/images/users/avatar-1.jpg"))
                    .titrePause(f.getTitrePause())
                    .dureePauseMinutes(f.getDureePauseMinutes())
                    .besoinSponsor(f.getBesoinSponsor())
                    .categorieId(f.getCategorie() != null ? f.getCategorie().getId() : null)
                    .categorieNom(f.getCategorie() != null ? f.getCategorie().getNom() : null) // ✅ ajoute cette ligne
                    .sponsorId(f.getSponsor() != null ? f.getSponsor().getId() : null)
                    .build();
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formation> getById(@PathVariable Long id) {
        return formationService.getFormationById(id)
                .map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Formation> updateFormation(
            @PathVariable Long id,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "publicationDate", required = false) String publicationDate,
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "lieu", required = false) String lieu,
            @RequestParam(value = "meetLink", required = false) String meetLink,
            @RequestParam(value = "formateurId", required = false) Long formateurId,
            @RequestParam(value = "categorieId", required = false) Long categorieId,
            @RequestParam(value = "titrePause", required = false) String titrePause,
            @RequestParam(value = "dureePauseMinutes", required = false) Integer dureePauseMinutes,
            @RequestParam(value = "besoinSponsor", required = false) Boolean besoinSponsor,
            @RequestParam(value = "sponsorId", required = false) Long sponsorId
    ) {
        return formationService.getFormationById(id)
                .map(existingFormation -> {
                    try {
                        if (name != null) existingFormation.setTitre(name);
                        if (description != null) existingFormation.setDescription(description);
                        if (price != null) existingFormation.setPrix(price);
                        if (publicationDate != null) existingFormation.setDatePublication(LocalDateTime.parse(publicationDate));
                        if (dateDebut != null) existingFormation.setDateDebut(LocalDateTime.parse(dateDebut));
                        if (dateFin != null) existingFormation.setDateFin(LocalDateTime.parse(dateFin));

                        if (mode != null) {
                            boolean isOnline = "enligne".equalsIgnoreCase(mode);
                            existingFormation.setEnLigne(isOnline);
                            if (isOnline) {
                                if (meetLink != null) existingFormation.setMeetLink(meetLink);
                                existingFormation.setLieu(null);
                            } else {
                                if (lieu != null) existingFormation.setLieu(lieu);
                                existingFormation.setMeetLink(null);
                            }
                        }

                        if (formateurId != null) {
                            existingFormation.setFormateurId(formateurId);
                        } else {
                            existingFormation.setFormateurId(null);
                        }

                        if (categorieId != null) {
                            categorieRepository.findById(categorieId)
                                    .ifPresent(existingFormation::setCategorie);
                        }

                        if (besoinSponsor != null) {
                            existingFormation.setBesoinSponsor(besoinSponsor);
                            if (besoinSponsor) {
                                if (titrePause != null) existingFormation.setTitrePause(titrePause);
                                if (dureePauseMinutes != null) existingFormation.setDureePauseMinutes(dureePauseMinutes);
                                if (sponsorId != null) {
                                    sponsorRepository.findById(sponsorId)
                                            .ifPresent(existingFormation::setSponsor);
                                }
                            } else {
                                existingFormation.setTitrePause(null);
                                existingFormation.setDureePauseMinutes(null);
                                existingFormation.setSponsor(null);
                            }
                        }

                        if (image != null && !image.isEmpty()) {
                            String imageUrl = cloudinaryService.uploadImage(image);
                            existingFormation.setImageUrl(imageUrl);
                        }

                        Formation updatedFormation = formationService.updateFormation(existingFormation);
                        return new ResponseEntity<>(updatedFormation, HttpStatus.OK);

                    } catch (Exception e) {
                        return new ResponseEntity<Formation>(HttpStatus.BAD_REQUEST);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    @GetMapping("/formateur/{formateurId}")
    public ResponseEntity<List<FormationResponseDTO>> getFormationsByFormateur(@PathVariable Long formateurId) {
        List<Formation> formations = formationService.getFormationsByFormateurId(formateurId);

        List<FormationResponseDTO> dtos = formations.stream().map(f -> {
            Map<String, Object> formateurData = formationService.getFormateurData(f.getFormateurId());

            return FormationResponseDTO.builder()
                    .id(f.getId())
                    .titre(f.getTitre())
                    .description(f.getDescription())
                    .prix(f.getPrix())
                    .imageUrl(f.getImageUrl())
                    .enLigne(f.isEnLigne())
                    .lieu(f.getLieu())
                    .meetLink(f.getMeetLink())
                    .dateDebut(f.getDateDebut())
                    .dateFin(f.getDateFin())
                    .datePublication(f.getDatePublication())
                    .formateurId(f.getFormateurId())
                    .formateurNom((String) formateurData.getOrDefault("nom", ""))
                    .formateurPrenom((String) formateurData.getOrDefault("prenom", ""))
                    .formateurImage((String) formateurData.getOrDefault("image", "assets/images/users/avatar-1.jpg"))
                    .titrePause(f.getTitrePause())
                    .dureePauseMinutes(f.getDureePauseMinutes())
                    .besoinSponsor(f.getBesoinSponsor())
                    .categorieId(f.getCategorie() != null ? f.getCategorie().getId() : null)
                    .sponsorId(f.getSponsor() != null ? f.getSponsor().getId() : null)
                    .build();
        }).toList();

        return ResponseEntity.ok(dtos);
    }

}
