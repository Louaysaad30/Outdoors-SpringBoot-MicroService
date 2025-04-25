package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.dto.FormationRequest;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.entity.Sponsor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IFormationService {
    Formation addFormation(FormationRequest request, MultipartFile imageFile) throws IOException;
    List<Formation> getAllFormations();
    Optional<Formation> getFormationById(Long id);
    void deleteFormation(Long id);
    Formation updateFormation(Formation formation);
    Optional<Sponsor> suggestSponsorForFormation(String description);
    String generateBetterDescription(String rawDescription);

}