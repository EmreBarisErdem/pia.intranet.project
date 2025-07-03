package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toDTO (Event event);

    Event toEntity (EventDto eventDto);

    void updateEventFromDto(EventDto dto, @MappingTarget Event entity);
}
