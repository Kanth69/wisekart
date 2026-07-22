package in.wisekart.service;

import in.wisekart.dto.CategoryResponse;
import in.wisekart.dto.CreateCategoryRequest;
import in.wisekart.dto.UpdateCategoryRequest;
import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getActiveCategoryById(Long id);
}
