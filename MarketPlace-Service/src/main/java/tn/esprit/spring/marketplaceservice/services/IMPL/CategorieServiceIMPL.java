package tn.esprit.spring.marketplaceservice.services.IMPL;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.PCategorie;
import tn.esprit.spring.marketplaceservice.repository.PCategorieRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IPCategorieService;

import java.util.List;

@AllArgsConstructor
@Service
public class CategorieServiceIMPL implements IPCategorieService {

    PCategorieRepository categorieRepository;
    @Override
    public List<PCategorie> retrieveCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public PCategorie updateCategorie(PCategorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public PCategorie addCategorie(PCategorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public PCategorie retrieveCategorie(long idCategorie) {
        return categorieRepository.findById(idCategorie).orElse(null);
    }

    @Override
    public void removeCategorie(long idCategorie) {
         categorieRepository.deleteById(idCategorie);
    }


}
