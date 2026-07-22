package in.wisekart.repository;

import in.wisekart.entity.Brand;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Brand> findByIsActiveTrue();

    Optional<Brand> findByIdAndIsActiveTrue(Long id);

    List<Brand> findByIsActiveTrueAndNameContainingIgnoreCase(String keyword);
}
