package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison,Long> {
}
