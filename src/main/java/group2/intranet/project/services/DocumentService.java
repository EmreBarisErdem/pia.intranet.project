package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DocumentDto;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    List<DocumentDto> getAllDocuments();

    DocumentDto getDocumentById(Integer id);

    DocumentDto createDocument(DocumentDto documentDto);

    void deleteDocument(Integer id);

    DocumentDto saveDocument(DocumentDto dto) throws IOException;
}
