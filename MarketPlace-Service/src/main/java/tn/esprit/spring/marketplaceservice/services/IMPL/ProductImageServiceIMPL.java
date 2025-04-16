package tn.esprit.spring.marketplaceservice.services.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.marketplaceservice.entity.ProductImage;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.repository.ProductImageRepository;
import tn.esprit.spring.marketplaceservice.repository.ProduitRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProductImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceIMPL implements IProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Override
    public List<ProductImage> getAllProductImages() {
        return productImageRepository.findAll();
    }

    @Override
    public ProductImage getProductImageById(Long id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + id));
    }

    @Override
    public ProductImage addProductImage(ProductImage productImage) {
        if (productImage.getDisplayOrder() == null) {
            // Set display order to next available position if not specified
            Long productId = productImage.getProduit().getIdProduit();
            long count = productImageRepository.countByProduitIdProduit(productId);
            productImage.setDisplayOrder((int) (count + 1));
        }
        return productImageRepository.save(productImage);
    }

    @Override
    public ProductImage updateProductImage(ProductImage productImage) {
        if (!productImageRepository.existsById(productImage.getIdImage())) {
            throw new RuntimeException("Cannot update non-existent image with ID: " + productImage.getIdImage());
        }
        return productImageRepository.save(productImage);
    }

    @Override
    public void deleteProductImage(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProduitIdProduitOrderByDisplayOrderAsc(productId);
    }

    @Override
    @Transactional
    public ProductImage addImageToProduct(Long productId, String imageUrl) {
        Produit produit = produitRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductImage newImage = new ProductImage();
        newImage.setImageUrl(imageUrl);
        newImage.setProduit(produit);

        // Set display order to next position
        long count = productImageRepository.countByProduitIdProduit(productId);
        newImage.setDisplayOrder((int) (count + 1));

        return productImageRepository.save(newImage);
    }

    @Override
    @Transactional
    public void setMainProductImage(Long productId, Long imageId) {
        // Find the product
        Produit produit = produitRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Find the image
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        // Check if image belongs to this product
        if (!image.getProduit().getIdProduit().equals(productId)) {
            throw new RuntimeException("Image with id " + imageId + " does not belong to product with id " + productId);
        }

        // Set the main image URL on the product
        produit.setImageProduit(image.getImageUrl());
        produitRepository.save(produit);
    }

    @Override
    @Transactional
    public void reorderProductImages(Long productId, List<Long> imageIdsInOrder) {
        // Validate product exists
        if (!produitRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        // Get all images for this product
        List<ProductImage> productImages = productImageRepository.findByProduitIdProduit(productId);

        // Create a map of id -> image for fast lookup
        java.util.Map<Long, ProductImage> imageMap = new java.util.HashMap<>();
        for (ProductImage image : productImages) {
            imageMap.put(image.getIdImage(), image);
        }

        // Validate all requested image ids belong to this product
        for (Long imageId : imageIdsInOrder) {
            if (!imageMap.containsKey(imageId)) {
                throw new RuntimeException("Image with id " + imageId + " does not belong to product with id " + productId);
            }
        }

        // Update display order for each image
        List<ProductImage> updatedImages = new ArrayList<>();
        for (int i = 0; i < imageIdsInOrder.size(); i++) {
            Long imageId = imageIdsInOrder.get(i);
            ProductImage image = imageMap.get(imageId);
            image.setDisplayOrder(i + 1);
            updatedImages.add(image);
        }

        // Save all updated images
        productImageRepository.saveAll(updatedImages);
    }
}