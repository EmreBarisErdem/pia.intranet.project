package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.NewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@Log
@RestController
@RequestMapping(path = "/news", method = RequestMethod.POST)
public class NewsController {

    private NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getAll(){
        List<NewsDTO> newsList = newsService.getAllNews();

        if (newsList.isEmpty()){
            log.warning("News Not Found. News are empty!");
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        log.info("News successfully created!");
        return ResponseEntity.ok(newsList); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer newsId){
        NewsDTO news = newsService.getNewsById(newsId);

        if(news == null){
            log.warning("News could not be found with ID: " + newsId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(news); //200 OK
    }

    @GetMapping("/type/{newsType}")
    public ResponseEntity<List<NewsDTO>> getNewsByType(@PathVariable("newsType") String newsType){

        List<NewsDTO> newsList = newsService.getNewsByType(newsType);

        if(newsList.isEmpty()){
            log.info("News of type was not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(newsList); //200 OK
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getNewsImageById(@PathVariable Integer id) {

        NewsDTO news = newsService.getNewsById(id);

        if (news == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentType(MediaType.IMAGE_JPEG)
                .contentType(MediaType.IMAGE_GIF)
                .body(news.getCover_image());
    }


    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<NewsDTO> createNews(
            @ModelAttribute @Valid NewsDTO newsDTO,
            @RequestParam("file") MultipartFile file) {

        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            Integer id = loggedInEmployee.getId();

            newsDTO.setCreatedById(id);
            newsDTO.setCover_image(file.getBytes());

            NewsDTO savedNews = newsService.saveNews(newsDTO);

            if (savedNews == null){
                log.info("News could not be saved. Returned dto is null.");
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).build();
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);

        } catch (Exception e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NewsDTO> updateNews(
            @PathVariable Integer id,
            @ModelAttribute @Valid NewsDTO newsDto,
            @RequestParam("file") MultipartFile file )
    {

        if (file == null || file.isEmpty()) {
            log.info("No document was uploaded.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            id = loggedInEmployee.getId();

            NewsDTO existingNews = newsService.getNewsById(id);

            newsDto.setCover_image(file.getBytes());
            newsDto.setCreatedById(id);

            if (existingNews == null) {
                log.warning("News to be updated not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            NewsDTO updatedNews = newsService.updateNews(id, newsDto);

            if(updatedNews == null){
                log.warning("News not updated with ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            return ResponseEntity.ok().body(updatedNews);

        } catch (Exception e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
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
