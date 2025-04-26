package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Panier;
import tn.esprit.spring.marketplaceservice.entity.Produit;

import java.util.List;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande,Long> {
    public LigneCommande findByPanierAndProduit(Panier panier, Produit produit);
    List<LigneCommande> findByCommandeIdCommande(Long idCommande);
}
