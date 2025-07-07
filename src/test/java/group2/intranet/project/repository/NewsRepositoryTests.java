package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.News;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.repositories.NewsRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.repositories.DepartmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NewsRepositoryTests {
    private final NewsRepository newsRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public NewsRepositoryTests(NewsRepository newsRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.newsRepository = newsRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    
    private Employee testEmployee;
    private Department testDepartment;

    @BeforeEach
    public void setUp() {
        // Create test department first
        testDepartment = Department.builder()
                .name("Test Department")
                .email("test@company.com")
                .location("Test Location")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        // Create test employee
        testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@company.com")
                .department(testDepartment)
                .role("EMPLOYEE")
                .passwordHash("hashedpassword")
                .build();
        testEmployee = employeeRepository.save(testEmployee);
    }


    @Test
    public void NewsRepository_SaveAll_ReturnsSavedNews() {
        News news = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        News savedNews = newsRepository.save(news);

        Assertions.assertThat(savedNews).isNotNull();
        Assertions.assertThat(savedNews.getId()).isGreaterThan(0);
    }

    @Test
    public void NewsRepository_GetAll_ReturnsMoreThenOneNews() {
        News news = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        News news2 = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);
        newsRepository.save(news2);

        List<News> newsList = newsRepository.findAll();

        Assertions.assertThat(newsList).isNotNull();
        Assertions.assertThat(newsList.size()).isEqualTo(2);
    }

    @Test
    public void NewsRepository_FindById_ReturnsSavedNews() {
        News news = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);

        News newsReturn = newsRepository.findById(news.getId()).get();

        Assertions.assertThat(newsReturn).isNotNull();
    }

    @Test
    public void NewsRepository_UpdateNews_ReturnNews() {
        News news = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);

        News newsSave = newsRepository.findById(news.getId()).get();

        newsSave.setTitle("test");
        newsSave.setContent("test");
        newsSave.setNewsType("test");
        newsSave.setCreatedAt(dateTime);

        News updatedNews = newsRepository.save(newsSave);

        Assertions.assertThat(updatedNews.getTitle()).isNotNull();
        Assertions.assertThat(updatedNews.getContent()).isNotNull();
        Assertions.assertThat(updatedNews.getCreatedBy()).isNotNull();
        Assertions.assertThat(updatedNews.getNewsType()).isNotNull();
        Assertions.assertThat(updatedNews.getCreatedAt()).isNotNull();
    }

    @Test
    public void NewsRepository_NewsDelete_ReturnNewsIsEmpty() {
        News news = News.builder()
                .title("title")
                .content("content")
                .newsType("type")
                .createdBy(testEmployee)
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);

        newsRepository.deleteById(news.getId());
        Optional<News> newsReturn = newsRepository.findById(news.getId());

        Assertions.assertThat(newsReturn).isEmpty();
    }

}
