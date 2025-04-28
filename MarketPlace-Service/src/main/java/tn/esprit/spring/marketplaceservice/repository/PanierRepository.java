package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Panier;

import java.util.List;

public interface PanierRepository extends JpaRepository<Panier,Long> {
    Panier findByUserId(Long userId);
    List<Panier> findAllByUserId(Long userId);
}
