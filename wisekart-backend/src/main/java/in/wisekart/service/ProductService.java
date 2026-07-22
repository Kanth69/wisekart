package in.wisekart.service;

import in.wisekart.dto.CreateProductRequest;
import in.wisekart.dto.ProductResponse;
import in.wisekart.dto.UpdateProductRequest;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> getActiveProducts();

    ProductResponse getActiveProductById(Long id);

    org.springframework.data.domain.Page<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            Long brandId,
            int page,
            int size,
            String sortBy,
            String sortDirection);
}
