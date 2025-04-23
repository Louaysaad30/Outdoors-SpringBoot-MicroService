package tn.esprit.spring.formationservice.services.interfaces;

import tn.esprit.spring.formationservice.entity.Categorie;

import java.util.List;

public interface ICategorieService {
    Categorie addCategorie(Categorie categorie);
    List<Categorie> getAllCategories();
    Categorie getCategorieById(Long id);
    void deleteCategorie(Long id);
}
