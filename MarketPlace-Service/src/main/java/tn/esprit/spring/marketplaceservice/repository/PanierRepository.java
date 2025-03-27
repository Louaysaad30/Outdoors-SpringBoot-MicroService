package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Panier;

public interface PanierRepository extends JpaRepository<Panier,Long> {
    Panier findByUserId(Long userId);
}
