package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.PCategorie;

import java.util.List;

public interface IPCategorieService {
    List<PCategorie> retrieveCategories();
    PCategorie addCategorie(PCategorie categorie);
    PCategorie updateCategorie(PCategorie categorie);
    PCategorie retrieveCategorie(long idCategorie);
    void removeCategorie(long idCategorie);

}
