package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Document;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.DocumentMapper;
import group2.intranet.project.repositories.DocumentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.services.DocumentServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTests {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private DocumentMapper documentMapper; // Real mapper, not mocked

    private DocumentServiceImpl documentService;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        documentMapper = Mappers.getMapper(DocumentMapper.class);
        // Manually inject dependencies
        documentService = new DocumentServiceImpl(documentMapper, documentRepository, employeeRepository);
    }

    @Test
    public void DocumentService_SaveDocument_ReturnsDocumentDto() throws Exception {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee uploadedBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        MultipartFile mockFile = new MockMultipartFile(
                "test.pdf",
                "test.pdf",
                "application/pdf",
                "Test PDF content".getBytes()
        );

        DocumentDto documentDto = DocumentDto.builder()
                .title("Test Document")
                .description("Test Description")
                .documentType("PDF")
                .uploadedById(100)
                .file(mockFile)
                .departmentIds(List.of()) // Add empty department list
                .build();

        Document savedDocument = Document.builder()
                .id(1)
                .title("Test Document")
                .description("Test Description")
                .documentType("PDF")
                .fileData("Test PDF content".getBytes())
                .uploadedAt(dateTime)
                .uploadedBy(uploadedBy)
                .departments(List.of()) // Empty list instead of null
                .build();

        // Mocks
        when(documentRepository.save(Mockito.any(Document.class))).thenReturn(savedDocument);

        // Act
        DocumentDto result = documentService.saveDocument(documentDto);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Test Document");
        Assertions.assertThat(result.getDescription()).isEqualTo("Test Description");
        Assertions.assertThat(result.getDocumentType()).isEqualTo("PDF");
        Assertions.assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    public void DocumentService_GetAllDocuments_ReturnsDocumentDtoList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee uploadedBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        Document document1 = Document.builder()
                .id(1)
                .title("Document 1")
                .description("Description 1")
                .documentType("PDF")
                .fileData("Content 1".getBytes())
                .uploadedAt(dateTime)
                .uploadedBy(uploadedBy)
                .departments(List.of()) // Empty list instead of null
                .build();

        Document document2 = Document.builder()
                .id(2)
                .title("Document 2")
                .description("Description 2")
                .documentType("DOCX")
                .fileData("Content 2".getBytes())
                .uploadedAt(dateTime)
                .uploadedBy(uploadedBy)
                .departments(List.of()) // Empty list instead of null
                .build();

        List<Document> documents = List.of(document1, document2);

        // Mocks
        when(documentRepository.findAll()).thenReturn(documents);

        // Act
        List<DocumentDto> result = documentService.getAllDocuments();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("Document 1");
        Assertions.assertThat(result.get(0).getDocumentType()).isEqualTo("PDF");
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo("Document 2");
        Assertions.assertThat(result.get(1).getDocumentType()).isEqualTo("DOCX");
    }

    @Test
    public void DocumentService_GetDocumentById_ReturnsDocumentDto() {
        // Arrange
        Integer documentId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee uploadedBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        Document document = Document.builder()
                .id(documentId)
                .title("Test Document")
                .description("Test Description")
                .documentType("PDF")
                .fileData("Test content".getBytes())
                .uploadedAt(dateTime)
                .uploadedBy(uploadedBy)
                .departments(List.of()) // Empty list instead of null
                .build();

        // Mocks
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // Act
        DocumentDto result = documentService.getDocumentById(documentId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Test Document");
        Assertions.assertThat(result.getDescription()).isEqualTo("Test Description");
        Assertions.assertThat(result.getDocumentType()).isEqualTo("PDF");
        Assertions.assertThat(result.getId()).isEqualTo(documentId);
    }

    @Test
    public void DocumentService_GetDocumentById_ReturnsNullWhenNotFound() {
        // Arrange
        Integer documentId = 999;

        // Mocks
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // Act
        DocumentDto result = documentService.getDocumentById(documentId);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void DocumentService_CreateDocument_ReturnsCreatedDocumentDto() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee uploadedBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        DocumentDto documentDto = DocumentDto.builder()
                .title("New Document")
                .description("New Description")
                .documentType("PDF")
                .uploadedById(100)
                .departmentIds(List.of()) // Add empty department list
                .build();

        Document savedDocument = Document.builder()
                .id(1)
                .title("New Document")
                .description("New Description")
                .documentType("PDF")
                .uploadedAt(dateTime)
                .uploadedBy(uploadedBy)
                .departments(List.of()) // Empty list instead of null
                .build();

        // Mocks
        when(documentRepository.save(Mockito.any(Document.class))).thenReturn(savedDocument);

        // Act
        DocumentDto result = documentService.createDocument(documentDto);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("New Document");
        Assertions.assertThat(result.getDescription()).isEqualTo("New Description");
        Assertions.assertThat(result.getDocumentType()).isEqualTo("PDF");
        Assertions.assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    public void DocumentService_DeleteDocument_CallsRepositoryDelete() {
        // Arrange
        Integer documentId = 1;

        // Act
        documentService.deleteDocument(documentId);

        // Assert
        verify(documentRepository, times(1)).deleteById(documentId);
    }
}
