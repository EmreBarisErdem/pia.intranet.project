package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.NewsDTO;

import java.util.List;

public interface NewsService {

    public List<NewsDTO> getAllNews();

    public NewsDTO getNewsById(Integer id);

    public List<NewsDTO> getNewsByType(String type);

}
