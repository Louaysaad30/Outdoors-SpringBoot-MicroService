package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande,Long> {
}
