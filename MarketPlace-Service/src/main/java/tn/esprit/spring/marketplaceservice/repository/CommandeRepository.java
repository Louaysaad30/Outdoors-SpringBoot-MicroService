package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Commande;

public interface CommandeRepository extends JpaRepository<Commande,Long> {
}
