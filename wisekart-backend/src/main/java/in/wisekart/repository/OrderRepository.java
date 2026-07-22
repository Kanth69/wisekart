package in.wisekart.repository;

import in.wisekart.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);
}
