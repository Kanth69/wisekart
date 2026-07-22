package in.wisekart.service;

import in.wisekart.dto.BrandResponse;
import in.wisekart.dto.CreateBrandRequest;
import in.wisekart.dto.UpdateBrandRequest;
import in.wisekart.entity.Brand;
import in.wisekart.exception.DuplicateResourceException;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.BrandRepository;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request) {
        String name = request.getName().trim();
        ensureNameIsAvailable(name, null);
        String slug = generateSlug(name);
        ensureSlugIsAvailable(slug, null);

        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setIsActive(Boolean.TRUE);

        return toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, UpdateBrandRequest request) {
        Brand brand = getBrandById(id);
        String name = request.getName().trim();
        ensureNameIsAvailable(name, id);
        String slug = generateSlug(name);
        ensureSlugIsAvailable(slug, id);

        brand.setName(name);
        brand.setSlug(slug);
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        if (request.getIsActive() != null) {
            brand.setIsActive(request.getIsActive());
        }

        return toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = getBrandById(id);
        brand.setIsActive(Boolean.FALSE);
        brandRepository.save(brand);
    }

    @Override
    public List<BrandResponse> getActiveBrands() {
        return brandRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    public BrandResponse getActiveBrandById(Long id) {
        Brand brand = brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active brand not found with id: " + id));
        return toResponse(brand);
    }

    @Override
    public List<BrandResponse> searchActiveBrandsByName(String keyword) {
        return brandRepository.findByIsActiveTrueAndNameContainingIgnoreCase(keyword.trim()).stream()
                .map(this::toResponse)
                .toList();
    }

    private Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
    }

    private void ensureNameIsAvailable(String name, Long brandId) {
        boolean alreadyExists = brandId == null
                ? brandRepository.existsByNameIgnoreCase(name)
                : brandRepository.existsByNameIgnoreCaseAndIdNot(name, brandId);
        if (alreadyExists) {
            throw new DuplicateResourceException("A brand with this name already exists");
        }
    }

    private void ensureSlugIsAvailable(String slug, Long brandId) {
        boolean alreadyExists = brandId == null
                ? brandRepository.existsBySlug(slug)
                : brandRepository.existsBySlugAndIdNot(slug, brandId);
        if (alreadyExists) {
            throw new DuplicateResourceException("A brand with this slug already exists");
        }
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Brand name cannot produce an empty slug");
        }
        return normalized;
    }

    private BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
