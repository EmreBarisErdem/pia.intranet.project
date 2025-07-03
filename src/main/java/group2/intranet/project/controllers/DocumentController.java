package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.services.DocumentService;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@RequestBody DocumentDto documentDto) {
        DocumentDto createdDocument = documentService.createDocument(documentDto);

        if (createdDocument == null) {
            log.warning("Document creation failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Document created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id){
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
