package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DocumentDto;

import java.util.List;

public interface DocumentService {

    List<DocumentDto> getAllDocuments();

    DocumentDto getDocumentById(Integer id);

    public DocumentDto createDocument(DocumentDto documentDto);

    public void deleteDocument(Integer id);
}
