package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.News;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.NewsMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.repositories.NewsRepository;
import group2.intranet.project.services.NewsServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTests {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private DepartmentRepository departmentRepository;
   
    @Mock
    private EmployeeRepository employeeRepository;

    private NewsMapper newsMapper; // Real mapper, not mocked

    private NewsServiceImpl newsService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        newsMapper = Mappers.getMapper(NewsMapper.class);
        // Manually inject dependencies
        newsService = new NewsServiceImpl(newsMapper, newsRepository, employeeRepository, departmentRepository);
        
        // Create mock employees
        employee1 = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@company.com")
                .build();
                
        employee2 = Employee.builder()
                .id(101)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@company.com")
                .build();
    }

    @Test
    public void NewsService_GetAllNews_ReturnsNewsDTOList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        News news1 = News.builder()
                .id(1)
                .title("News 1")
                .content("Content 1")
                .newsType("General")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        News news2 = News.builder()
                .id(2)
                .title("News 2")
                .content("Content 2")
                .newsType("Urgent")
                .createdBy(employee2)
                .createdAt(dateTime)
                .build();

        List<News> newsList = List.of(news1, news2);

        // Mocks
        when(newsRepository.findAll()).thenReturn(newsList);

        // Act
        List<NewsDTO> result = newsService.getAllNews();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("News 1");
        Assertions.assertThat(result.get(0).getNewsType()).isEqualTo("General");
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo("News 2");
        Assertions.assertThat(result.get(1).getNewsType()).isEqualTo("Urgent");
    }

    @Test
    public void NewsService_GetNewsById_ReturnsNewsDTO() {
        // Arrange
        Integer newsId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        News news = News.builder()
                .id(newsId)
                .title("Test News")
                .content("Test Content")
                .newsType("General")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        // Mocks
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));

        // Act
        NewsDTO result = newsService.getNewsById(newsId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Test News");
        Assertions.assertThat(result.getContent()).isEqualTo("Test Content");
        Assertions.assertThat(result.getNewsType()).isEqualTo("General");
        // Note: Assuming NewsDTO has createdById as Integer, adjust based on actual DTO structure
    }

    @Test
    public void NewsService_GetNewsById_ReturnsNullWhenNotFound() {
        // Arrange
        Integer newsId = 999;

        // Mocks
        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // Act
        NewsDTO result = newsService.getNewsById(newsId);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void NewsService_GetNewsByType_ReturnsFilteredNewsList() {
        // Arrange
        String newsType = "Urgent";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        News news1 = News.builder()
                .id(1)
                .title("Urgent News 1")
                .content("Urgent Content 1")
                .newsType("Urgent")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        News news2 = News.builder()
                .id(2)
                .title("Urgent News 2")
                .content("Urgent Content 2")
                .newsType("Urgent")
                .createdBy(employee2)
                .createdAt(dateTime)
                .build();

        List<News> urgentNewsList = List.of(news1, news2);

        // Mocks
        when(newsRepository.findByNewsType(newsType)).thenReturn(urgentNewsList);

        // Act
        List<NewsDTO> result = newsService.getNewsByType(newsType);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getNewsType()).isEqualTo("Urgent");
        Assertions.assertThat(result.get(1).getNewsType()).isEqualTo("Urgent");
    }

    @Test
    public void NewsService_GetNewsByType_ReturnsNullWhenNoNewsFound() {
        // Arrange
        String newsType = "NonExistent";

        // Mocks
        when(newsRepository.findByNewsType(newsType)).thenReturn(List.of());

        // Act
        List<NewsDTO> result = newsService.getNewsByType(newsType);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void NewsService_SaveNews_ReturnsNewsSavedDTO() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        NewsDTO newsDTO = NewsDTO.builder()
                .title("New News")
                .content("New Content")
                .newsType("General")
                .createdById(100) // This should be an Integer ID
                .build();

        News savedNews = News.builder()
                .id(1)
                .title("New News")
                .content("New Content")
                .newsType("General")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        // Mocks - Need to mock employee repository to return employee when ID is looked up
        when(employeeRepository.findById(100L)).thenReturn(Optional.of(employee1));
        when(newsRepository.saveAndFlush(Mockito.any(News.class))).thenReturn(savedNews);

        // Act
        NewsDTO result = newsService.saveNews(newsDTO);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("New News");
        Assertions.assertThat(result.getContent()).isEqualTo("New Content");
        Assertions.assertThat(result.getNewsType()).isEqualTo("General");
        Assertions.assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    public void NewsService_UpdateNews_ReturnsUpdatedNewsDTO() {
        // Arrange
        Integer newsId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        News existingNews = News.builder()
                .id(newsId)
                .title("Original Title")
                .content("Original Content")
                .newsType("General")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        NewsDTO updateDTO = NewsDTO.builder()
                .title("Updated Title")
                .content("Updated Content")
                .newsType("Urgent")
                .createdById(100) // Add createdById since the service expects it
                .build();

        News updatedNews = News.builder()
                .id(newsId)
                .title("Updated Title")
                .content("Updated Content")
                .newsType("Urgent")
                .createdBy(employee1)
                .createdAt(dateTime)
                .build();

        // Mocks
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(existingNews));
        when(employeeRepository.findById(100L)).thenReturn(Optional.of(employee1)); // Mock employee lookup for update
        when(newsRepository.saveAndFlush(Mockito.any(News.class))).thenReturn(updatedNews);

        // Act
        NewsDTO result = newsService.updateNews(newsId, updateDTO);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Updated Title");
        Assertions.assertThat(result.getContent()).isEqualTo("Updated Content");
        Assertions.assertThat(result.getNewsType()).isEqualTo("Urgent");
    }

    @Test
    public void NewsService_UpdateNews_ReturnsNullWhenNotFound() {
        // Arrange
        Integer newsId = 999;
        NewsDTO updateDTO = NewsDTO.builder()
                .title("Updated Title")
                .build();

        // Mocks
        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // Act
        NewsDTO result = newsService.updateNews(newsId, updateDTO);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void NewsService_DeleteNews_CallsRepositoryDelete() {
        // Arrange
        Integer newsId = 1;

        // Act
        newsService.deleteNews(newsId);

        // Assert
        verify(newsRepository, times(1)).deleteById(newsId);
    }
}
