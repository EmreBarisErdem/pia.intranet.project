package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Document;
import group2.intranet.project.mappers.DocumentMapper;
import group2.intranet.project.repositories.DocumentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
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
    private EmployeeRepository employeeRepository;
    private DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentMapper documentMapper, DocumentRepository documentRepository, EmployeeRepository employeeRepository) {
        this.documentMapper = documentMapper;
        this.documentRepository = documentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public DocumentDto saveDocument(DocumentDto dto){

        try {

            Document documentEntity = documentMapper.toEntity(dto);

            documentEntity.setFileData(dto.getFile().getBytes()); // PDF dosyasını DB'ye koy..

            Document savedDocument = documentRepository.save(documentEntity);

            return documentMapper.toDto(savedDocument);

        } catch (Exception e) {
            log.warning("Error while saving Document : " + e.getMessage());
            return null;
        }
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
            log.info("Error while creating document : Error Message" + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteDocument(Integer id) {
        documentRepository.deleteById(id);
    }
}
