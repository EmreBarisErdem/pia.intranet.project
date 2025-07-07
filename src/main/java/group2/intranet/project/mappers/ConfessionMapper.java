package group2.intranet.project.mappers;


import group2.intranet.project.domain.dtos.ConfessionDto;
import group2.intranet.project.domain.entities.Confession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ConfessionMapper {

    ConfessionDto toDTO(Confession confession);

    Confession toEntity(ConfessionDto confessionDto);
}
