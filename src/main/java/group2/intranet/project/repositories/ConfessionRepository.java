package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.Confession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfessionRepository extends JpaRepository<Confession, Integer> {
}
