package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsDTO toDto(News news);

    News toEntity(NewsDTO newsDTO);

    void updateNewsFromDto(NewsDTO dto, @MappingTarget News entity);
}
