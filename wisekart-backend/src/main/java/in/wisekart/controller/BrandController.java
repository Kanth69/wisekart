package in.wisekart.controller;

import in.wisekart.dto.BrandResponse;
import in.wisekart.dto.CreateBrandRequest;
import in.wisekart.dto.UpdateBrandRequest;
import in.wisekart.service.BrandService;
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
public class BrandController {

    private final BrandService brandService;

    @PostMapping("/api/admin/brands")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        BrandResponse brand = brandService.createBrand(request);
        return ResponseEntity.created(URI.create("/api/brands/" + brand.getId())).body(brand);
    }

    @PutMapping("/api/admin/brands/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Long id, @Valid @RequestBody UpdateBrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @DeleteMapping("/api/admin/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/brands")
    public ResponseEntity<List<BrandResponse>> getActiveBrands() {
        return ResponseEntity.ok(brandService.getActiveBrands());
    }

    @GetMapping("/api/brands/{id}")
    public ResponseEntity<BrandResponse> getActiveBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getActiveBrandById(id));
    }
}
