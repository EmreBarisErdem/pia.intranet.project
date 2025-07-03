package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentDto toDto(Document document);

    Document toEntity(DocumentDto document);
}
