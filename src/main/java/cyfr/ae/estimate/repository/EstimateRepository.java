package cyfr.ae.estimate.repository;

import cyfr.ae.estimate.model.Estimate;
import cyfr.ae.estimate.model.Project;
import cyfr.ae.estimate.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Integer> {
    Page<Estimate> findByProject(Project project, Pageable pageable);
    Page<Estimate> findByEstimator(User estimator, Pageable pageable);
    Page<Estimate> findByStatus(String status, Pageable pageable);
}