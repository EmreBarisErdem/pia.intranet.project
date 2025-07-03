package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.News;
import group2.intranet.project.mappers.NewsMapper;
import group2.intranet.project.repositories.NewsRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log
public class NewsServiceImpl implements NewsService {

    private NewsMapper newsMapper;
    private NewsRepository newsRepository;

    public NewsServiceImpl(NewsMapper newsMapper, NewsRepository newsRepository) {
        this.newsMapper = newsMapper;
        this.newsRepository = newsRepository;
    }

    @Override
    public List<NewsDTO> getAllNews() {

        List<News> newsList = newsRepository.findAll();

        return newsList.stream()
                .map(newsMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public NewsDTO getNewsById(Integer id) {
        News newsById = newsRepository.findById(id).orElse(null);

        if(newsById == null){
            return null;
        }

        return newsMapper.toDto(newsById);
    }

    @Override
    public List<NewsDTO> getNewsByType(String type) {


        List<News> newsListByType = newsRepository.findByNewsType(type);

        if(newsListByType.isEmpty()){
            log.info("Not Found By Declared Type");
            return null;
        }

        return newsListByType.stream()
                .map(newsMapper::toDto)
                .collect(Collectors.toList());
    }
}
