package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.formationservice.entity.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
