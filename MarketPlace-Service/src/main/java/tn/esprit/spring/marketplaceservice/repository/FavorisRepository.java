package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Favoris;

public interface FavorisRepository extends JpaRepository<Favoris,Long> {
}
