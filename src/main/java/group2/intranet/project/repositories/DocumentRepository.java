package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Integer> {


}
