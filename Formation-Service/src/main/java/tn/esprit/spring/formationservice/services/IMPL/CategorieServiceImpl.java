package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.formationservice.entity.Categorie;
import tn.esprit.spring.formationservice.repository.CategorieRepository;
import tn.esprit.spring.formationservice.services.interfaces.ICategorieService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements ICategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public Categorie addCategorie(Categorie categorie) {
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
