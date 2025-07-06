package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Document;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.DocumentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DocumentRepositoryTests {
    private DocumentRepository documentRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private Employee testEmployee;
    private Department testDepartment;

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    LocalDate testLocalDate = LocalDate.parse("2025-04-08 12:30", formatter);

    @Autowired
    public DocumentRepositoryTests(DocumentRepository documentRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.documentRepository = documentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @BeforeEach
    public void setup() {
        testDepartment = departmentRepository.save(
                Department.builder()
                        .name("Test Department")
                        .location("Test Location")
                        .email("test@department.com")
                        .build()
        );

        testEmployee = employeeRepository.save(
                Employee.builder()
                        .email("test@test.com")
                        .passwordHash("test")
                        .firstName("test")
                        .lastName("test")
                        .phoneNumber("+905350000000")
                        .department(testDepartment)
                        .jobTitle("Test Job Title")
                        .dateOfJoining(testLocalDate)
                        .birthday(testLocalDate)
                        .role("EMPLOYEE")
                        .createdAt(dateTime)
                        .build()
        );
    }

    private Document createAndSaveTestDocument(Integer number) {
        Document document = Document.builder()
                .title("Test Document " + number)
                .description("Test Description " + number)
                .fileData("Test file content".getBytes())
                .documentType("PDF")
                .uploadedBy(testEmployee)
                .build();
        return documentRepository.save(document);
    }

    @Test
    public void DocumentRepository_SaveAll_ReturnsSavedDocument() {
        Document savedTestDocument = createAndSaveTestDocument(1);

        Assertions.assertThat(savedTestDocument).isNotNull();
        Assertions.assertThat(savedTestDocument.getId()).isGreaterThan(0);
    }

    @Test
    public void DocumentRepository_GetAll_ReturnsMoreThenOneDocument() {
        createAndSaveTestDocument(1);
        createAndSaveTestDocument(2);

        List<Document> documentList = documentRepository.findAll();

        Assertions.assertThat(documentList).isNotNull();
        Assertions.assertThat(documentList.size()).isEqualTo(2);
    }

    @Test
    public void DocumentRepository_FindById_ReturnsSavedDocument() {
        Document savedTestDocument = createAndSaveTestDocument(1);

        Document documentReturn = documentRepository.findById(savedTestDocument.getId()).get();

        Assertions.assertThat(documentReturn).isNotNull();
    }

    @Test
    public void DocumentRepository_UpdateDocument_ReturnDocument() {
        Document savedTestDocument = createAndSaveTestDocument(1);

        Document documentSave = documentRepository.findById(savedTestDocument.getId()).get();

        documentSave.setTitle("Updated Document Title");
        documentSave.setDescription("Updated Description");
        documentSave.setFileData("Updated file content".getBytes());
        documentSave.setDocumentType("DOCX");

        Document updatedDocument = documentRepository.save(documentSave);

        Assertions.assertThat(updatedDocument.getTitle()).isNotNull();
        Assertions.assertThat(updatedDocument.getDescription()).isNotNull();
        Assertions.assertThat(updatedDocument.getFileData()).isNotNull();
        Assertions.assertThat(updatedDocument.getDocumentType()).isNotNull();
        Assertions.assertThat(updatedDocument.getTitle()).isEqualTo("Updated Document Title");
        Assertions.assertThat(updatedDocument.getDocumentType()).isEqualTo("DOCX");
    }

    @Test
    public void DocumentRepository_DocumentDelete_ReturnDocumentIsEmpty() {
        Document savedTestDocument = createAndSaveTestDocument(1);

        documentRepository.deleteById(savedTestDocument.getId());
        Optional<Document> documentReturn = documentRepository.findById(savedTestDocument.getId());

        Assertions.assertThat(documentReturn).isEmpty();
    }
}
