package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.CodeProduit;

public interface CodeProduitRepository extends JpaRepository<CodeProduit, Long> {
    CodeProduit findByCode(String code);
}
