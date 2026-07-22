package in.wisekart.service;

import in.wisekart.dto.CategoryResponse;
import in.wisekart.dto.CreateCategoryRequest;
import in.wisekart.dto.UpdateCategoryRequest;
import in.wisekart.entity.Category;
import in.wisekart.exception.DuplicateResourceException;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.CategoryRepository;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String name = request.getName().trim();
        ensureNameIsAvailable(name, null);
        String slug = generateSlug(name);
        ensureSlugIsAvailable(slug, null);

        Category category = new Category();
        category.setName(name);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSlug(slug);
        category.setIsActive(Boolean.TRUE);

        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryById(id);
        String name = request.getName().trim();
        ensureNameIsAvailable(name, id);
        String slug = generateSlug(name);
        ensureSlugIsAvailable(slug, id);

        category.setName(name);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSlug(slug);
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsActive(Boolean.FALSE);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    public CategoryResponse getActiveCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active category not found with id: " + id));
        return toResponse(category);
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private void ensureNameIsAvailable(String name, Long categoryId) {
        boolean alreadyExists = categoryId == null
                ? categoryRepository.existsByNameIgnoreCase(name)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, categoryId);
        if (alreadyExists) {
            throw new DuplicateResourceException("A category with this name already exists");
        }
    }

    private void ensureSlugIsAvailable(String slug, Long categoryId) {
        boolean alreadyExists = categoryId == null
                ? categoryRepository.existsBySlug(slug)
                : categoryRepository.existsBySlugAndIdNot(slug, categoryId);
        if (alreadyExists) {
            throw new DuplicateResourceException("A category with this slug already exists");
        }
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Category name cannot produce an empty slug");
        }
        return normalized;
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .slug(category.getSlug())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
