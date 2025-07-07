package group2.intranet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group2.intranet.project.controllers.NewsController;
import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.NewsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NewsController.class)
@ContextConfiguration(classes = {NewsController.class, NewsControllerTests.TestConfig.class, TestSecurityConfig.class})
public class NewsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public NewsService newsService() {
            return mock(NewsService.class);
        }
    }

    private NewsDTO newsDto1;
    private NewsDTO newsDto2;

    @BeforeEach
    void setup() {
        newsDto1 = NewsDTO.builder()
                .id(1)
                .title("Company Update")
                .content("Important company update for all employees.")
                .newsType("GENERAL")
                .createdAt(LocalDateTime.of(2025, 1, 15, 9, 0))
                .createdById(1)
                .build();

        newsDto2 = NewsDTO.builder()
                .id(2)
                .title("Policy Change")
                .content("New policy regarding remote work.")
                .newsType("POLICY")
                .createdAt(LocalDateTime.of(2025, 1, 16, 10, 30))
                .createdById(1)
                .build();
    }

    private void setupAuthenticationWithUserId(Long userId, String role) {
        Employee mockEmployee = new Employee();
        mockEmployee.setId(Math.toIntExact(userId));
        mockEmployee.setEmail("test@company.com");
        mockEmployee.setFirstName("Test");
        mockEmployee.setLastName("User");
        mockEmployee.setRole(role);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            mockEmployee, 
            "password", 
            List.of(new SimpleGrantedAuthority(role))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void NewsController_GetAll_ReturnsNewsList() throws Exception {
        // Arrange
        List<NewsDTO> newsList = Arrays.asList(newsDto1, newsDto2);
        when(newsService.getAllNews()).thenReturn(newsList);

        // Act & Assert
        mockMvc.perform(get("/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Company Update"))
                .andExpect(jsonPath("$[0].newsType").value("GENERAL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Policy Change"))
                .andExpect(jsonPath("$[1].newsType").value("POLICY"));
    }

    @Test
    @WithMockUser(roles = {"EMPLOYEE"})
    public void NewsController_GetAll_AsEmployee_ReturnsNewsList() throws Exception {
        // Arrange
        List<NewsDTO> newsList = Arrays.asList(newsDto1, newsDto2);
        when(newsService.getAllNews()).thenReturn(newsList);

        // Act & Assert
        mockMvc.perform(get("/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void NewsController_GetAll_ReturnsNoContent_WhenNoNews() throws Exception {
        // Arrange
        when(newsService.getAllNews()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void NewsController_GetById_ReturnsNews() throws Exception {
        // Arrange
        Integer newsId = 1;
        when(newsService.getNewsById(newsId)).thenReturn(newsDto1);

        // Act & Assert
        mockMvc.perform(get("/news/{id}", newsId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Company Update"))
                .andExpect(jsonPath("$.content").value("Important company update for all employees."))
                .andExpect(jsonPath("$.newsType").value("GENERAL"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void NewsController_GetById_ReturnsNotFound_WhenNewsNotExists() throws Exception {
        // Arrange
        Integer newsId = 999;
        when(newsService.getNewsById(newsId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/news/{id}", newsId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // @Test
    // public void NewsController_CreateNews_WithHRRole_ReturnsCreatedNews() throws Exception {
    //     // Arrange
    //     setupAuthenticationWithUserId(1L, "ROLE_HR");
        
    //     MockMultipartFile imageFile = new MockMultipartFile(
    //             "file",
    //             "news-image.jpg",
    //             "image/jpeg",
    //             "Test image content".getBytes()
    //     );

    //     NewsDTO createdNews = NewsDTO.builder()
    //             .id(3)
    //             .title("New Announcement")
    //             .content("This is a new announcement.")
    //             .newsType("GENERAL")
    //             .createdAt(LocalDateTime.now())
    //             .createdById(1)
    //             .cover_image("Test image content".getBytes())
    //             .build();

    //     when(newsService.saveNews(any(NewsDTO.class))).thenReturn(createdNews);

    //     // Act & Assert
    //     mockMvc.perform(multipart("/news/create")
    //                     .file(imageFile)
    //                     .param("title", "New Announcement")
    //                     .param("content", "This is a new announcement.")
    //                     .param("newsType", "GENERAL"))
    //             .andExpect(status().isCreated())
    //             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(jsonPath("$.id").value(3))
    //             .andExpect(jsonPath("$.title").value("New Announcement"))
    //             .andExpect(jsonPath("$.newsType").value("GENERAL"));

    //     verify(newsService, times(1)).saveNews(any(NewsDTO.class));
    // }

//     @Test
//     public void NewsController_CreateNewsWithImage_WithHRRole_ReturnsCreatedNews() throws Exception {
//         // Arrange
//         setupAuthenticationWithUserId(1L, "ROLE_HR");
        
//         MockMultipartFile imageFile = new MockMultipartFile(
//                 "file",
//                 "news-image.jpg",
//                 "image/jpeg",
//                 "Test image content".getBytes()
//         );

//         NewsDTO createdNews = NewsDTO.builder()
//                 .id(3)
//                 .title("News with Image")
//                 .content("News content with image")
//                 .newsType("ANNOUNCEMENT")
//                 .createdAt(LocalDateTime.now())
//                 .createdById(1)
//                 .cover_image("Test image content".getBytes())
//                 .build();

//         when(newsService.saveNews(any(NewsDTO.class))).thenReturn(createdNews);

//         // Act & Assert
//         mockMvc.perform(multipart("/news/create-with-image")
//                         .file(imageFile)
//                         .param("title", "News with Image")
//                         .param("content", "News content with image")
//                         .param("newsType", "ANNOUNCEMENT"))
//                 .andExpect(status().isCreated())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(3))
//                 .andExpect(jsonPath("$.title").value("News with Image"));
//     }

//     @Test
//     public void NewsController_UpdateNews_WithHRRole_ReturnsUpdatedNews() throws Exception {
//         // Arrange
//         setupAuthenticationWithUserId(1L, "ROLE_HR");
        
//         Integer newsId = 1;
//         NewsDTO updateRequest = NewsDTO.builder()
//                 .title("Updated News")
//                 .content("Updated content")
//                 .newsType("UPDATED")
//                 .build();

//         NewsDTO updatedNews = NewsDTO.builder()
//                 .id(1)
//                 .title("Updated News")
//                 .content("Updated content")
//                 .newsType("UPDATED")
//                 .createdAt(LocalDateTime.of(2025, 1, 15, 9, 0))
//                 .createdById(1)
//                 .build();

//         when(newsService.getNewsById(newsId)).thenReturn(newsDto1);
//         when(newsService.updateNews(eq(newsId), any(NewsDTO.class))).thenReturn(updatedNews);

//         // Act & Assert
//         mockMvc.perform(put("/news/update/{id}", newsId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(updateRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.title").value("Updated News"))
//                 .andExpect(jsonPath("$.content").value("Updated content"));
//     }

//     @Test
//     @WithMockUser(roles = {"HR"})
//     public void NewsController_DeleteNews_ReturnsOk() throws Exception {
//         // Arrange
//         Integer newsId = 1;
//         when(newsService.getNewsById(newsId)).thenReturn(newsDto1);
//         doNothing().when(newsService).deleteNews(newsId);

//         // Act & Assert
//         mockMvc.perform(delete("/news/delete/{id}", newsId))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(roles = {"HR"})
//     public void NewsController_DeleteNews_ReturnsNotFound_WhenNewsNotExists() throws Exception {
//         // Arrange
//         Integer newsId = 999;
//         when(newsService.getNewsById(newsId)).thenReturn(null);

//         // Act & Assert
//         mockMvc.perform(delete("/news/delete/{id}", newsId))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void NewsController_GetAll_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/news")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isForbidden());
//     }

//     @Test
//     @WithMockUser(roles = {"EMPLOYEE"})
//     public void NewsController_CreateNews_AsEmployee_ReturnsForbidden() throws Exception {
//         // Arrange
//         NewsDTO newNews = NewsDTO.builder()
//                 .title("New Announcement")
//                 .content("This is a new announcement.")
//                 .newsType("GENERAL")
//                 .build();

//         // Act & Assert
//         mockMvc.perform(post("/news/create")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(newNews)))
//                 .andExpect(status().isForbidden());
//     }

//     @Test
//     @WithMockUser(roles = {"EMPLOYEE"})
//     public void NewsController_DeleteNews_AsEmployee_ReturnsForbidden() throws Exception {
//         // Act & Assert
//         mockMvc.perform(delete("/news/delete/1"))
//                 .andExpect(status().isForbidden());
//     }
}
