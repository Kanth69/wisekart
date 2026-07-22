package in.wisekart.repository;

import in.wisekart.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Product> findByIsActiveTrue();

    Optional<Product> findByIdAndIsActiveTrue(Long id);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findByIsActiveTrue(Pageable pageable);
}
