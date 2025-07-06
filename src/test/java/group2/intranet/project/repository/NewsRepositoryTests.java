package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.News;
import group2.intranet.project.repositories.NewsRepository;
import org.assertj.core.api.Assertions;
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

    @Autowired
    public NewsRepositoryTests(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);


    @Test
    public void NewsRepository_SaveAll_ReturnsSavedNews() {
        News news = News.builder()
                .title("title")
                .content("content")
                .createdBy(1)
                .newsType("type")
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
                .createdBy(1)
                .newsType("type")
                .createdAt(dateTime)
                .build();

        News news2 = News.builder()
                .title("title")
                .content("content")
                .createdBy(1)
                .newsType("type")
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
                .createdBy(1)
                .newsType("type")
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
                .createdBy(1)
                .newsType("type")
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);

        News newsSave = newsRepository.findById(news.getId()).get();

        newsSave.setTitle("test");
        newsSave.setContent("test");
        newsSave.setCreatedBy(2);
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
                .createdBy(1)
                .newsType("type")
                .createdAt(dateTime)
                .build();

        newsRepository.save(news);

        newsRepository.deleteById(news.getId());
        Optional<News> newsReturn = newsRepository.findById(news.getId());

        Assertions.assertThat(newsReturn).isEmpty();
    }

}
