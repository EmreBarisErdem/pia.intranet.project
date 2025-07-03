package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
