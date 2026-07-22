package in.wisekart.repository;

import in.wisekart.entity.Address;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndId(Long userId, Long id);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
