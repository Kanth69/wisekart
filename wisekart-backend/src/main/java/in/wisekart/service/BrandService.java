package in.wisekart.service;

import in.wisekart.dto.BrandResponse;
import in.wisekart.dto.CreateBrandRequest;
import in.wisekart.dto.UpdateBrandRequest;
import java.util.List;

public interface BrandService {

    BrandResponse createBrand(CreateBrandRequest request);

    BrandResponse updateBrand(Long id, UpdateBrandRequest request);

    void deleteBrand(Long id);

    List<BrandResponse> getActiveBrands();

    BrandResponse getActiveBrandById(Long id);

    List<BrandResponse> searchActiveBrandsByName(String keyword);
}
