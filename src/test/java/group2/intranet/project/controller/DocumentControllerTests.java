package group2.intranet.project.controller;

import group2.intranet.project.controllers.DocumentController;
import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.DocumentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class)
@ContextConfiguration(classes = {DocumentController.class, DocumentControllerTests.TestConfig.class, TestSecurityConfig.class})
public class DocumentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentService documentService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DocumentService documentService() {
            return mock(DocumentService.class);
        }
    }

    private DocumentDto documentDto1;
    private DocumentDto documentDto2;

    @BeforeEach
    void setup() {
        documentDto1 = DocumentDto.builder()
                .id(1)
                .title("Test Document 1")
                .description("Test Description 1")
                .documentType("PDF")
                .uploadedAt(LocalDateTime.of(2025, 1, 15, 10, 30))
                .uploadedById(1)
                .fileData("Test PDF content 1".getBytes())
                .departmentIds(List.of(1, 2))
                .build();

        documentDto2 = DocumentDto.builder()
                .id(2)
                .title("Test Document 2")
                .description("Test Description 2")
                .documentType("DOCX")
                .uploadedAt(LocalDateTime.of(2025, 1, 16, 11, 45))
                .uploadedById(2)
                .fileData("Test DOCX content 2".getBytes())
                .departmentIds(List.of(1))
                .build();
    }

    private void setupAuthenticationWithUserId(Long userId) {
        Employee mockEmployee = new Employee();
        mockEmployee.setId(Math.toIntExact(userId));
        mockEmployee.setEmail("test@company.com");
        mockEmployee.setFirstName("Test");
        mockEmployee.setLastName("User");
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            mockEmployee, 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_HR"))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_GetAll_ReturnsDocumentList() throws Exception {
        // Arrange
        List<DocumentDto> documents = Arrays.asList(documentDto1, documentDto2);
        when(documentService.getAllDocuments()).thenReturn(documents);

        // Act & Assert
        mockMvc.perform(get("/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Document 1"))
                .andExpect(jsonPath("$[0].documentType").value("PDF"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Test Document 2"))
                .andExpect(jsonPath("$[1].documentType").value("DOCX"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_GetAll_ReturnsNoContent_WhenNoDocuments() throws Exception {
        // Arrange
        when(documentService.getAllDocuments()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_GetById_ReturnsDocument() throws Exception {
        // Arrange
        Integer documentId = 1;
        when(documentService.getDocumentById(documentId)).thenReturn(documentDto1);

        // Act & Assert
        mockMvc.perform(get("/documents/{id}", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Document 1"))
                .andExpect(jsonPath("$.description").value("Test Description 1"))
                .andExpect(jsonPath("$.documentType").value("PDF"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_GetById_ReturnsNotFound_WhenDocumentNotFound() throws Exception {
        // Arrange
        Integer documentId = 999;
        when(documentService.getDocumentById(documentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/documents/{id}", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_GetById_ThrowsConstraintViolationException_WhenInvalidId() throws Exception {
        // Act & Assert - expecting ServletException wrapping ConstraintViolationException when validation fails
        try {
            mockMvc.perform(get("/documents/{id}", 0)
                            .contentType(MediaType.APPLICATION_JSON));
            // If we get here, the test should fail
            org.junit.jupiter.api.Assertions.fail("Expected ServletException to be thrown");
        } catch (jakarta.servlet.ServletException e) {
            // Verify that the cause is ConstraintViolationException
            assertTrue(e.getCause() instanceof jakarta.validation.ConstraintViolationException);
            
            // Verify the error message
            jakarta.validation.ConstraintViolationException constraintEx = 
                (jakarta.validation.ConstraintViolationException) e.getCause();
            assertTrue(constraintEx.getMessage().contains("ID must be greater than or equal to 1"));
        }
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_DownloadDocument_ReturnsFileData() throws Exception {
        // Arrange
        Integer documentId = 1;
        when(documentService.getDocumentById(documentId)).thenReturn(documentDto1);

        // Act & Assert
        mockMvc.perform(get("/documents/download/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"Test Document 1.pdf\""))
                .andExpect(content().bytes("Test PDF content 1".getBytes()));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_DownloadDocument_ReturnsNoContent_WhenNoFileData() throws Exception {
        // Arrange
        Integer documentId = 1;
        DocumentDto documentWithoutFile = DocumentDto.builder()
                .id(1)
                .title("Test Document")
                .fileData(null)
                .build();
        when(documentService.getDocumentById(documentId)).thenReturn(documentWithoutFile);

        // Act & Assert
        mockMvc.perform(get("/documents/download/{id}", documentId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void DocumentController_UploadDocument_ReturnsCreatedDocument() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Test PDF content".getBytes()
        );

        DocumentDto savedDocument = DocumentDto.builder()
                .id(1)
                .title("Uploaded Document")
                .description("Uploaded Description")
                .documentType("PDF")
                .uploadedAt(LocalDateTime.now())
                .uploadedById(1)
                .fileData("Test PDF content".getBytes())
                .build();

        when(documentService.saveDocument(any(DocumentDto.class))).thenReturn(savedDocument);

        // Act & Assert
        mockMvc.perform(multipart("/documents/upload")
                        .file(file)
                        .param("title", "Uploaded Document")
                        .param("description", "Uploaded Description")
                        .param("documentType", "PDF")
                        .param("uploadedById", "1"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Uploaded Document"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_UploadDocument_ReturnsBadRequest_WhenNoFile() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/documents/upload")
                        .param("title", "Test Document")
                        .param("description", "Test Description"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_UploadDocument_ReturnsBadRequest_WhenEmptyFile() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/documents/upload")
                        .file(emptyFile)
                        .param("title", "Test Document")
                        .param("description", "Test Description"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"EMPLOYEE"})
    public void DocumentController_UploadDocument_ReturnsForbidden_WhenNotHR() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Test PDF content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/documents/upload")
                        .file(file)
                        .param("title", "Test Document")
                        .param("description", "Test Description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_DeleteDocument_ReturnsOk() throws Exception {
        // Arrange
        Integer documentId = 1;
        when(documentService.getDocumentById(documentId)).thenReturn(documentDto1);
        doNothing().when(documentService).deleteDocument(documentId);

        // Act & Assert
        mockMvc.perform(delete("/documents/delete/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Document deleted successfully."));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void DocumentController_DeleteDocument_ReturnsNotFound_WhenDocumentNotExists() throws Exception {
        // Arrange
        Integer documentId = 999;
        when(documentService.getDocumentById(documentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/documents/delete/{id}", documentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"EMPLOYEE"})
    public void DocumentController_DeleteDocument_ReturnsForbidden_WhenNotHR() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/documents/delete/{id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void DocumentController_GetAll_ReturnsForbidden_WhenNotAuthenticated() throws Exception {
        // Act & Assert - should return 403 when no authentication is provided
        mockMvc.perform(get("/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
