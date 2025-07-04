package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.services.NewsService;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Validated
@Log
@RestController
@RequestMapping(path = "/news")
public class NewsController {

    private NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getAll(){
        List<NewsDTO> newsList = newsService.getAllNews();

        if (newsList.isEmpty()){
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(newsList); // 200 OK
    }

    @GetMapping("{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer newsId){
        NewsDTO news = newsService.getNewsById(newsId);

        if(news == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(news); //200 OK
    }

    @GetMapping("/type/{newsType}")
    public ResponseEntity<List<NewsDTO>> getNewsByType(@PathVariable("newsType") String newsType){

        List<NewsDTO> newsList = newsService.getNewsByType(newsType);

        if(newsList.isEmpty()){
            log.info("");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(newsList); //200 OK
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        NewsDTO news = newsService.getNewsById(id);
        if (news == null || news.getCoverImage() == null)
            return ResponseEntity.notFound().build();

        byte[] imageBytes = Base64.getDecoder().decode(news.getCoverImage());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NewsDTO> createNews(
          @RequestParam("title") String title,
          @RequestParam("content") String content,
          @RequestParam("createdBy") Integer createdBy,
          @RequestParam("newsType") String newsType,
          @RequestParam("file") MultipartFile file)
    {

        try {
            NewsDTO dto = NewsDTO.builder()
                    .title(title)
                    .content(content)
                    .createdBy(createdBy)
                    .newsType(newsType)
                    .coverImage(Base64.getEncoder().encodeToString(file.getBytes()))
                    .build();

            NewsDTO savedNews = newsService.saveNews(dto);

            if (savedNews == null){
                log.info("News could not be saved");
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).build();
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);

        } catch (IOException e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NewsDTO> updateNews(@PathVariable Integer id,
                                              @RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam("newsType") String newsType,
                                              @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            NewsDTO existingNews = newsService.getNewsById(id);
            if (existingNews == null) {
                return ResponseEntity.notFound().build();
            }

            existingNews.setTitle(title);
            existingNews.setContent(content);
            existingNews.setNewsType(newsType);

            if (file != null && !file.isEmpty()) {
                existingNews.setCoverImage(Base64.getEncoder().encodeToString(file.getBytes()));
            }

            NewsDTO updatedNews = newsService.updateNews(id, existingNews);
            return ResponseEntity.ok(updatedNews);

        } catch (IOException e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Integer id) {
        NewsDTO existingNews = newsService.getNewsById(id);

        if (existingNews == null) {
            log.warning("Event to delete not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        newsService.deleteNews(id);
        log.info("Deleted news with ID: " + id);
        return ResponseEntity.ok("News deleted successfully"); // 204
    }


}
