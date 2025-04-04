package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormationServiceImpl implements IFormationService {

    private final FormationRepository formationRepository;
    private final CategorieRepository categorieRepository;
    private final SponsorRepository sponsorRepository;
    private final ICloudinaryService cloudinaryService;

    @Override
    public Formation addFormation(FormationRequest request, MultipartFile imageFile) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(imageFile);

        Formation.FormationBuilder formationBuilder = Formation.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .prix(request.getPrix())
                .imageUrl(imageUrl)
                .formateurId(request.getFormateurId())
                .enLigne("enligne".equalsIgnoreCase(request.getMode()))
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .datePublication(LocalDateTime.now());

        // Catégorie
        if (request.getCategorieId() != null) {
            Optional<Categorie> categorieOpt = categorieRepository.findById(request.getCategorieId());
            categorieOpt.ifPresent(formationBuilder::categorie);
        }

        // Si mode en ligne, ajouter le lien meet
        if ("enligne".equalsIgnoreCase(request.getMode())) {
            formationBuilder.meetLink("https://meet.google.com/new"); // Peut être généré dynamiquement
        } else {
            formationBuilder.lieu(request.getLieu());
        }

        // Pause optionnelle
        if (request.getPauseTitle() != null && request.getPauseDuration() != null) {
            formationBuilder.titrePause(request.getPauseTitle())
                    .dureePauseMinutes(request.getPauseDuration())
                    .besoinSponsor(request.isPauseSponsorRequired());

            if (request.getSponsorId() != null) {
                Optional<Sponsor> sponsorOpt = sponsorRepository.findById(request.getSponsorId());
                sponsorOpt.ifPresent(formationBuilder::sponsor);
            }
        }

        return formationRepository.save(formationBuilder.build());
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
}