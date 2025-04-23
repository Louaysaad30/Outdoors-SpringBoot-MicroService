package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.marketplaceservice.entity.ProductImage;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Find all images for a specific product ID ordered by display order
    List<ProductImage> findByProduitIdProduitOrderByDisplayOrderAsc(Long idProduit);

    // Find all images for a specific product ID
    List<ProductImage> findByProduitIdProduit(Long idProduit);

    // Delete all images for a specific product ID (useful when deleting a product)
    void deleteByProduitIdProduit(Long idProduit);

    // Count images for a product (could be useful for validation)
    long countByProduitIdProduit(Long idProduit);

    // Find the first image for a product (could be used as default main image)
    ProductImage findFirstByProduitIdProduitOrderByDisplayOrderAsc(Long idProduit);
}