package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Categorie;

import java.io.IOException;
import java.util.List;

public interface ICategorieService {
    Categorie addCategorie(Categorie categorie, MultipartFile imageFile) throws IOException;
    List<Categorie> getAllCategories();
    Categorie getCategorieById(Long id);
    void deleteCategorie(Long id);
}