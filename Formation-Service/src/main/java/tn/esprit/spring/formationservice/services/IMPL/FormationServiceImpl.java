package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Formation;
import tn.esprit.spring.formationservice.repository.FormationRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.formationservice.services.interfaces.IFormationService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormationServiceImpl implements IFormationService {

    private final FormationRepository formationRepository;
    private final ICloudinaryService cloudinaryService;

    @Override
    public Formation addFormation(Formation formation, MultipartFile image) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(image);
        formation.setImageUrl(imageUrl);
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
    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }

    @Override
    public Formation updateFormation(Formation formation) {
        return formationRepository.save(formation);
    }
}