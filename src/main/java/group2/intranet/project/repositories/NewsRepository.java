package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Integer> {

    List<News> findByNewsType(String newsType);

}
