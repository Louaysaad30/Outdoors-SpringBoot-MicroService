package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.Status;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande,Long> {

    List<Commande> findByUserIdAndEtat(Long userId, Status etat);
}
