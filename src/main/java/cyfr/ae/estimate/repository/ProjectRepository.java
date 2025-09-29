package cyfr.ae.estimate.repository;

import cyfr.ae.estimate.model.Project;
import cyfr.ae.estimate.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Page<Project> findAllByClient(User client, Pageable pageable);
    Page<Project> findByStatus(String status, Pageable pageable);
}