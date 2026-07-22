package in.wisekart.service;

import in.wisekart.dto.CreateProductImageRequest;
import in.wisekart.dto.ProductImageResponse;
import in.wisekart.dto.UpdateProductImageRequest;
import in.wisekart.entity.Product;
import in.wisekart.entity.ProductImage;
import in.wisekart.exception.DuplicateResourceException;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.ProductImageRepository;
import in.wisekart.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductImageResponse createProductImage(Long productId, CreateProductImageRequest request) {
        Product product = getProductById(productId);
        validateDisplayOrder(request.getDisplayOrder());

        if (request.getIsPrimary()) {
            unsetExistingPrimaryImage(productId);
        }

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(request.getImageUrl());
        image.setAltText(request.getAltText());
        image.setDisplayOrder(request.getDisplayOrder());
        image.setIsPrimary(request.getIsPrimary());

        return toResponse(productImageRepository.save(image));
    }

    @Override
    @Transactional
    public ProductImageResponse updateProductImage(Long productId, Long imageId, UpdateProductImageRequest request) {
        Product product = getProductById(productId);
        ProductImage image = getProductImageByIdAndProduct(imageId, product);
        validateDisplayOrder(request.getDisplayOrder());

        if (request.getIsPrimary()) {
            unsetExistingPrimaryImage(productId);
        }

        image.setImageUrl(request.getImageUrl());
        image.setAltText(request.getAltText());
        image.setDisplayOrder(request.getDisplayOrder());
        image.setIsPrimary(request.getIsPrimary());

        return toResponse(productImageRepository.save(image));
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        Product product = getProductById(productId);
        ProductImage image = getProductImageByIdAndProduct(imageId, product);
        productImageRepository.delete(image);
    }

    @Override
    public List<ProductImageResponse> getProductImages(Long productId) {
        getProductById(productId);
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private ProductImage getProductImageByIdAndProduct(Long imageId, Product product) {
        return productImageRepository.findById(imageId)
                .filter(image -> image.getProduct().getId().equals(product.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + imageId + " for product id: " + product.getId()));
    }

    private void unsetExistingPrimaryImage(Long productId) {
        productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .ifPresent(image -> {
                    image.setIsPrimary(Boolean.FALSE);
                    productImageRepository.save(image);
                });
    }

    private void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder == null || displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be zero or positive");
        }
    }

    private ProductImageResponse toResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .altText(image.getAltText())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.getIsPrimary())
                .productId(image.getProduct().getId())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}
