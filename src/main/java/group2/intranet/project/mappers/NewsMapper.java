package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsDTO toDto(News news);

    News toEntity(NewsDTO newsDTO);

    void updateNewsFromDto(NewsDTO dto, @MappingTarget News entity);

    default String encodeImage(byte[] imageData) {
        return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
    }

    default byte[] decodeImage(String base64Image) {
        return base64Image != null ? Base64.getDecoder().decode(base64Image) : null;
    }
}
