package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.NewsDTO;

import java.util.List;

public interface NewsService {

    List<NewsDTO> getAllNews();

    NewsDTO getNewsById(Integer id);

    List<NewsDTO> getNewsByType(String type);

    NewsDTO saveNews(NewsDTO newsDTO);

    void deleteNews(Integer id);

    NewsDTO updateNews(Integer id, NewsDTO newsToBeUpdated);
}
