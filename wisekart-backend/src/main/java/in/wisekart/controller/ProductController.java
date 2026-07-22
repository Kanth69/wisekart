package in.wisekart.controller;

import in.wisekart.dto.CreateProductRequest;
import in.wisekart.dto.ProductResponse;
import in.wisekart.dto.UpdateProductRequest;
import in.wisekart.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/admin/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/products")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @GetMapping("/api/products/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseEntity.ok(productService.searchProducts(keyword, categoryId, brandId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> getActiveProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getActiveProductById(id));
    }
}
