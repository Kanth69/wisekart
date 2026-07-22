package in.wisekart.service;

import in.wisekart.dto.CreateProductImageRequest;
import in.wisekart.dto.ProductImageResponse;
import in.wisekart.dto.UpdateProductImageRequest;
import java.util.List;

public interface ProductImageService {

    ProductImageResponse createProductImage(Long productId, CreateProductImageRequest request);

    ProductImageResponse updateProductImage(Long productId, Long imageId, UpdateProductImageRequest request);

    void deleteProductImage(Long productId, Long imageId);

    List<ProductImageResponse> getProductImages(Long productId);
}
