package cyfr.ae.estimate.repository;

import cyfr.ae.estimate.model.Subsection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsectionRepository extends JpaRepository<Subsection, Integer> {
}