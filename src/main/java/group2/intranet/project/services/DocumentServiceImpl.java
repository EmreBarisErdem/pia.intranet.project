package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Document;
import group2.intranet.project.mappers.DocumentMapper;
import group2.intranet.project.repositories.DocumentRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log
public class DocumentServiceImpl implements DocumentService{

    private DocumentRepository documentRepository;
    private DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentMapper documentMapper, DocumentRepository documentRepository) {
        this.documentMapper = documentMapper;
        this.documentRepository = documentRepository;
    }

    @Override
    public List<DocumentDto> getAllDocuments() {
        List<Document> docs = documentRepository.findAll();

        return docs.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDto getDocumentById(Integer id) {
        Document document = documentRepository.findById(id).orElse(null);

        return documentMapper.toDto(document);
    }

    @Override
    public DocumentDto createDocument(DocumentDto documentDto) {
        try {
            Document documentEntity = documentMapper.toEntity(documentDto);
            Document result = documentRepository.save(documentEntity);

            if (result.getId() == null) {
                log.info("Event creation failed. ID is null after save.");
                return null;
            }

            return documentMapper.toDto(result);

        } catch (Exception e) {
            log.info("Error while creating event : Error Message" + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteDocument(Integer id) {
        documentRepository.deleteById(id);
    }
}
