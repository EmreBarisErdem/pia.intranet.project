package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.services.NewsService;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
