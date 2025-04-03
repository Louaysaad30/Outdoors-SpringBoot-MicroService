package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Formation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IFormationService {
    Formation addFormation(Formation formation, MultipartFile image) throws IOException;
    List<Formation> getAllFormations();
    Optional<Formation> getFormationById(Long id);
    void deleteFormation(Long id);
    Formation updateFormation(Formation formation);
}