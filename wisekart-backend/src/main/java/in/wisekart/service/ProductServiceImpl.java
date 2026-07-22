package in.wisekart.service;

import in.wisekart.dto.CreateProductRequest;
import in.wisekart.dto.ProductResponse;
import in.wisekart.dto.UpdateProductRequest;
import in.wisekart.entity.Brand;
import in.wisekart.entity.Category;
import in.wisekart.entity.Product;
import in.wisekart.exception.DuplicateResourceException;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.BrandRepository;
import in.wisekart.repository.CategoryRepository;
import in.wisekart.repository.ProductRepository;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String name = request.getName().trim();
        String sku = request.getSku().trim().toUpperCase(Locale.ROOT);
        ensureSkuIsAvailable(sku, null);
        validatePriceValues(request.getPrice(), request.getDiscountPrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));

        String slug = generateUniqueSlug(name, null);

        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setCategory(category);
        product.setBrand(brand);
        product.setIsActive(Boolean.TRUE);

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = getProductById(id);
        String name = request.getName().trim();
        String sku = request.getSku().trim().toUpperCase(Locale.ROOT);
        ensureSkuIsAvailable(sku, id);
        validatePriceValues(request.getPrice(), request.getDiscountPrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));

        String slug = generateUniqueSlug(name, id);

        product.setName(name);
        product.setSku(sku);
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setCategory(category);
        product.setBrand(brand);
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setIsActive(Boolean.FALSE);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponse getActiveProductById(Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with id: " + id));
        return toResponse(product);
    }

    @Override
    public Page<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            Long brandId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        if (normalizedKeyword != null && normalizedKeyword.isBlank()) {
            normalizedKeyword = null;
        }
        String normalizedSortBy = sortBy == null ? "createdAt" : sortBy;
        if (!SUPPORTED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + normalizedSortBy);
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDirection == null ? "DESC" : sortDirection);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortBy));

        Specification<Product> specification = Specification.where(isActive())
                .and(normalizedKeyword != null ? nameContains(normalizedKeyword) : null)
                .and(categoryId != null ? categoryIdEquals(categoryId) : null)
                .and(brandId != null ? brandIdEquals(brandId) : null);

        return productRepository.findAll(specification, pageable).map(this::toResponse);
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void ensureSkuIsAvailable(String sku, Long productId) {
        boolean alreadyExists = productId == null
                ? productRepository.existsBySku(sku)
                : productRepository.existsBySkuAndIdNot(sku, productId);
        if (alreadyExists) {
            throw new DuplicateResourceException("A product with this SKU already exists");
        }
    }

    private Specification<Product> isActive() {
        return (root, query, builder) -> builder.isTrue(root.get("isActive"));
    }

    private Specification<Product> nameContains(String keyword) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("name")),
                "%" + keyword.toLowerCase(Locale.ROOT) + "%");
    }

    private Specification<Product> categoryIdEquals(Long categoryId) {
        return (root, query, builder) -> builder.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Product> brandIdEquals(Long brandId) {
        return (root, query, builder) -> builder.equal(root.get("brand").get("id"), brandId);
    }

    private static final Set<String> SUPPORTED_SORT_FIELDS = Set.of("createdAt", "price", "name", "stock");

    private void validatePriceValues(BigDecimal price, BigDecimal discountPrice) {
        if (discountPrice != null && price != null && discountPrice.compareTo(price) > 0) {
            throw new IllegalArgumentException("Discount price must be less than or equal to price");
        }
    }

    private String generateUniqueSlug(String name, Long productId) {
        String baseSlug = normalizeToSlug(name);
        String slug = baseSlug;
        int suffix = 1;

        while (productRepository.existsBySlugAndIdNot(slug, productId == null ? -1L : productId)) {
            slug = String.format("%s-%d", baseSlug, suffix++);
        }

        return slug;
    }

    private String normalizeToSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Product name cannot produce an empty slug");
        }
        return normalized;
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stock(product.getStock())
                .thumbnailUrl(product.getThumbnailUrl())
                .categoryId(product.getCategory().getId())
                .brandId(product.getBrand().getId())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
