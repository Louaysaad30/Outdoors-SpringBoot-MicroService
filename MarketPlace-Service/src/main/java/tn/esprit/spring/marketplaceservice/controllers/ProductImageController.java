package tn.esprit.spring.marketplaceservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.ProductImage;
import tn.esprit.spring.marketplaceservice.services.interfaces.IProductImageService;

import java.util.List;

@Tag(name = "GestionImage")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/ProductImage")
public class ProductImageController {
    private final IProductImageService productImageService;

    @GetMapping
    @Operation(summary = "Get all product images", description = "Retrieve a list of all product images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all product images"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductImage>> getAllProductImages() {
        List<ProductImage> images = productImageService.getAllProductImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product image by ID", description = "Retrieve a specific product image by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product image"),
            @ApiResponse(responseCode = "404", description = "Product image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductImage> getProductImageById(@PathVariable Long id) {
        try {
            ProductImage image = productImageService.getProductImageById(id);
            return ResponseEntity.ok(image);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get images by product ID", description = "Retrieve all images for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product images"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductImage>> getImagesByProductId(@PathVariable Long productId) {
        List<ProductImage> images = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/product/{productId}")
    @Operation(summary = "Add image to product", description = "Add a new image to a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added image to product"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductImage> addImageToProduct(
            @PathVariable Long productId,
            @RequestBody String imageUrl) {
        try {
            ProductImage newImage = productImageService.addImageToProduct(productId, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(newImage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete product image", description = "Delete a specific product image by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted product image"),
            @ApiResponse(responseCode = "404", description = "Product image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long imageId) {
        try {
            productImageService.deleteProductImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{imageId}")
    @Operation(summary = "Update product image", description = "Update a specific product image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated product image"),
            @ApiResponse(responseCode = "404", description = "Product image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductImage> updateProductImage(
            @PathVariable Long imageId,
            @RequestBody ProductImage productImage) {
        try {
            // Ensure ID is set properly
            productImage.setIdImage(imageId);
            ProductImage updatedImage = productImageService.updateProductImage(productImage);
            return ResponseEntity.ok(updatedImage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{productId}/main-image/{imageId}")
    @Operation(summary = "Set main product image", description = "Set a specific image as the main image for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully set main product image"),
            @ApiResponse(responseCode = "404", description = "Product or image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> setMainProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            productImageService.setMainProductImage(productId, imageId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{productId}/reorder")
    @Operation(summary = "Reorder product images", description = "Change the display order of product images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully reordered product images"),
            @ApiResponse(responseCode = "404", description = "Product or image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> reorderProductImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIdsInOrder) {
        try {
            productImageService.reorderProductImages(productId, imageIdsInOrder);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}