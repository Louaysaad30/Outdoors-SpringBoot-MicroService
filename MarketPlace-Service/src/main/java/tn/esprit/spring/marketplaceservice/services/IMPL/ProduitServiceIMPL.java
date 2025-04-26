package tn.esprit.spring.marketplaceservice.services.IMPL;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.CodeProduit;
import tn.esprit.spring.marketplaceservice.entity.PCategorie;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.repository.CodeProduitRepository;
import tn.esprit.spring.marketplaceservice.repository.PCategorieRepository;
import tn.esprit.spring.marketplaceservice.repository.ProduitRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProduitService;

import java.util.List;

@AllArgsConstructor
@Service
public class ProduitServiceIMPL implements IProduitService {
    ProduitRepository produitRepository;
    PCategorieRepository pCategorieRepository;
    CodeProduitRepository codeProduitRepository;
    @Override
    public List<Produit> retrieveProduits() {
        return produitRepository.findAll();
    }

    @Override
    public Produit updateProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Override
    public Produit addProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Override
    public Produit retrieveProduit(long idProduit) {
        return produitRepository.findById(idProduit).orElse(null);
    }

    @Override
    public void removeProduit(long idProduit) {
            produitRepository.deleteById(idProduit);
    }

    @Override
    public Produit affecterProduitCategorie(long idProduit, long idCategorie) {
        PCategorie pCategorie = pCategorieRepository.findById(idCategorie).orElse(null);
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        produit.setCategorie(pCategorie);
        return produitRepository.save(produit);
    }

    @Override
    public Produit desaffecterProduitCategorie(long idProduit) {
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        produit.setCategorie(null);
        return produitRepository.save(produit);

    }

    @Override
    public Produit affecterProduitCode(long idProduit, long idCode) {
        CodeProduit codeProduit = codeProduitRepository.findById(idCode).orElse(null);
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        produit.setCodeProduit(codeProduit);
        return produitRepository.save(produit);
    }

    @Override
    public Produit desaffecterProduitCode(long idProduit) {
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        produit.setCodeProduit(null);
        return produitRepository.save(produit);
    }

    @Transactional
    @Override
    public List<Produit> getProduitsByCode(Long code) {
        return produitRepository.findByCodeProduitIdCodeProduit(code);
    }


}
