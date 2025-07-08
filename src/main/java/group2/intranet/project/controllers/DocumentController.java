package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.DocumentService;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Log
@RestController
@RequestMapping(path = "/documents")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAll(){
        List<DocumentDto> docs = documentService.getAllDocuments();

        if (docs.isEmpty()){
            log.info("Document not found");
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        log.info("Documanlar Gönderildi");
        return ResponseEntity.ok(docs); // 200 OK
    }

    @GetMapping("{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer docId){
        DocumentDto document = documentService.getDocumentById(docId);

        if(document == null){
            log.info("Document not found with ID: " + docId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(document); //200 OK
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Integer id) {
        DocumentDto documentDto = documentService.getDocumentById(id);

        if (documentDto.getFileData() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentDto.getTitle() + ".pdf\"")
                .body(documentDto.getFileData());
    }


    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> uploadDocument(@ModelAttribute DocumentDto documentDTO){

        if (documentDTO.getFile() == null || documentDTO.getFile().isEmpty()) {
            log.info("No document was uploaded.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            Integer userId = loggedInEmployee.getId();

            documentDTO.setUploadedById(Math.toIntExact(userId));

            DocumentDto savedDocument = documentService.saveDocument(documentDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedDocument);

        } catch (Exception e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentDTO);
        }

    }

//    /// Document without file
//    @PostMapping
//    public ResponseEntity<DocumentDto> createDocument(@RequestBody @Valid DocumentDto documentDto) {
//        DocumentDto createdDocument = documentService.createDocument(documentDto);
//
//        if (createdDocument == null) {
//            log.warning("Document creation failed.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        log.info("Document created successfully");
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
//    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Integer id){
        DocumentDto existingDocument = documentService.getDocumentById(id);

        if (existingDocument == null) {
            log.warning("Document to delete not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        documentService.deleteDocument(id);
        log.info("Deleted document with ID: " + id);
        return ResponseEntity.ok("Document deleted successfully.");
    }
}
