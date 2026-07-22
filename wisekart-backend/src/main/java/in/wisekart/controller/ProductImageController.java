package in.wisekart.controller;

import in.wisekart.dto.CreateProductImageRequest;
import in.wisekart.dto.ProductImageResponse;
import in.wisekart.dto.UpdateProductImageRequest;
import in.wisekart.service.ProductImageService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/api/admin/products/{productId}/images")
    public ResponseEntity<ProductImageResponse> createProductImage(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductImageRequest request) {
        ProductImageResponse image = productImageService.createProductImage(productId, request);
        return ResponseEntity.created(URI.create("/api/products/" + productId + "/images/" + image.getId())).body(image);
    }

    @PutMapping("/api/admin/products/{productId}/images/{imageId}")
    public ResponseEntity<ProductImageResponse> updateProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @Valid @RequestBody UpdateProductImageRequest request) {
        return ResponseEntity.ok(productImageService.updateProductImage(productId, imageId, request));
    }

    @DeleteMapping("/api/admin/products/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/products/{productId}/images")
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImages(productId));
    }
}
