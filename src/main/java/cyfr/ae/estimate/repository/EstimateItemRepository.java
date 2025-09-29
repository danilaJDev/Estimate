package cyfr.ae.estimate.repository;

import cyfr.ae.estimate.model.EstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateItemRepository extends JpaRepository<EstimateItem, Integer> {
}