package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.domain.entities.News;
import group2.intranet.project.mappers.NewsMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
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
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;

    public NewsServiceImpl(NewsMapper newsMapper, NewsRepository newsRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.newsMapper = newsMapper;
        this.newsRepository = newsRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
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
        News newsById = newsRepository.findById(id)
                .orElse(null);

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

    @Override
    public NewsDTO saveNews(NewsDTO newsDTO) {

        try {
            News newsEntity = newsMapper.toEntity(newsDTO);

            Employee creator = employeeRepository.findById(Long.valueOf(newsDTO.getCreatedById()))
                    .orElse(null);

            newsEntity.setCreatedBy(creator);

//            List<Department> newsDepartments = departmentRepository.findAllById(newsDTO.getDepartmentIds());
//
//            newsEntity.setDepartments(newsDepartments);


            News savedNews = newsRepository.saveAndFlush(newsEntity);

            return newsMapper.toDto(savedNews);

        } catch (Exception e) {
            log.warning("Error occured while saving News" + e.getMessage());
            return null;
        }



    }

    @Override
    public NewsDTO updateNews(Integer id, NewsDTO newsToBeUpdated) {
        try {
            News existingNews = newsRepository.findById(id).orElse(null);

            newsMapper.updateNewsFromDto(newsToBeUpdated,existingNews);

            assert existingNews != null;

            Employee creator = employeeRepository.findById(Long.valueOf(newsToBeUpdated.getCreatedById()))
                    .orElse(null);

            existingNews.setCreatedBy(creator);

//            List<Department> newsDepartments = departmentRepository.findAllById(newsToBeUpdated.getDepartmentIds());
//
//            existingNews.setDepartments(newsDepartments);

            News savedNews = newsRepository.saveAndFlush(existingNews);

            return newsMapper.toDto(savedNews);
        }
        catch (Exception e){
            log.info("Error Occured While Updateing News In the Service Layer" + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }
}
