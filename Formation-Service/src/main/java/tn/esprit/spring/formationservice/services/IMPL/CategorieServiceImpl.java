package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Categorie;
import tn.esprit.spring.formationservice.repository.CategorieRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICategorieService;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements ICategorieService {

    private final CategorieRepository categorieRepository;
    private final ICloudinaryService cloudinaryService;

    @Override
    public Categorie addCategorie(Categorie categorie, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            categorie.setImageUrl(imageUrl);
        }
        return categorieRepository.save(categorie);
    }

    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}