package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Produit;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit,Long> {
    List<Produit> findByCodeProduitIdCodeProduit(Long code);
}
