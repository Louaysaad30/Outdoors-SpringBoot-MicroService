package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.ProductImage;
import java.util.List;

public interface IProductImageService {

    // Basic CRUD operations
    List<ProductImage> getAllProductImages();
    ProductImage getProductImageById(Long id);
    ProductImage addProductImage(ProductImage productImage);  // Return the saved entity
    ProductImage updateProductImage(ProductImage productImage);  // Return the updated entity
    void deleteProductImage(Long id);

    // Additional useful methods
    List<ProductImage> getImagesByProductId(Long productId);
    ProductImage addImageToProduct(Long productId, String imageUrl);
    void setMainProductImage(Long productId, Long imageId);
    void reorderProductImages(Long productId, List<Long> imageIdsInOrder);
}