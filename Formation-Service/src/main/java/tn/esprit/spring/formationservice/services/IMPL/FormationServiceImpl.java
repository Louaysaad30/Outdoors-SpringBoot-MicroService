package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.dto.FormationRequest;
import tn.esprit.spring.formationservice.entity.Categorie;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.repository.CategorieRepository;
import tn.esprit.spring.formationservice.repository.FormationRepository;
import tn.esprit.spring.formationservice.repository.SponsorRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.formationservice.services.interfaces.IFormationService;
import tn.esprit.spring.formationservice.services.interfaces.IHuggingFaceService;
import tn.esprit.spring.formationservice.services.interfaces.ISponsorService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormationServiceImpl implements IFormationService {

    private final FormationRepository formationRepository;
    private final CategorieRepository categorieRepository;
    private final SponsorRepository sponsorRepository;
    private final ICloudinaryService cloudinaryService;
    private final ISponsorService sponsorService;
    private final IHuggingFaceService huggingFaceService;
    private final RestTemplate restTemplate = new RestTemplate(); // Ajoute si tu ne l'as pas déjà

    @Override
    public Formation addFormation(FormationRequest request, MultipartFile imageFile) throws IOException {
        // 1. Upload image
        String imageUrl = cloudinaryService.uploadImage(imageFile);

        // 2. Construire la Formation
        Formation.FormationBuilder formationBuilder = Formation.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .prix(request.getPrix())
                .imageUrl(imageUrl)
                .enLigne("enligne".equalsIgnoreCase(request.getMode()))
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .datePublication(LocalDateTime.now())
                .formateurId(request.getFormateurId());

        // 3. Lieu ou MeetLink selon mode
        if ("enligne".equalsIgnoreCase(request.getMode())) {
            formationBuilder.meetLink(request.getMeetLink());
        } else {
            formationBuilder.lieu(request.getLieu());
        }

        // 4. Lier catégorie
        if (request.getCategorieId() != null) {
            categorieRepository.findById(request.getCategorieId())
                    .ifPresent(formationBuilder::categorie);
        }

        // 5. Lier Sponsor pour la pause si nécessaire
        if (Boolean.TRUE.equals(request.isPauseSponsorRequired())) {
            if (request.getSponsorId() != null) {
                sponsorRepository.findById(request.getSponsorId())
                        .ifPresent(formationBuilder::sponsor);
            }
            formationBuilder.titrePause(request.getPauseTitle())
                    .dureePauseMinutes(request.getPauseDuration())
                    .besoinSponsor(true);
        } else {
            formationBuilder.besoinSponsor(false);
        }

        // 6. Sauvegarder
        Formation formation = formationBuilder.build();
        return formationRepository.save(formation);
    }

    @Override
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    @Override
    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }

    @Override
    public Formation updateFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    @Override
    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }

    @Override
    public Optional<Sponsor> suggestSponsorForFormation(String description) {
        return sponsorService.suggestBestSponsor(description);
    }

    @Override
    public String generateBetterDescription(String rawText) {
        return huggingFaceService.generateBetterDescription(rawText);
    }
    @Override
    public Map<String, Object> getFormateurData(Long formateurId) {
        if (formateurId == null) {
            return new HashMap<>();
        }
        try {
            String url = "http://localhost:9096/user/" + formateurId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> userData = response.getBody();
                Map<String, Object> data = new HashMap<>();
                data.put("nom", userData.get("nom"));
                data.put("prenom", userData.get("prenom"));
                data.put("image", userData.get("image"));
                return data;
            }
        } catch (Exception e) {
            System.out.println("Erreur récupération formateur: " + e.getMessage());
        }
        return new HashMap<>();
    }
    @Override
    public List<Formation> getFormationsByFormateurId(Long formateurId) {
        return formationRepository.findByFormateurId(formateurId);
    }


}
