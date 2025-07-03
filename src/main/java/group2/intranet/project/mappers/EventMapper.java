package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface EventMapper {
    @Mapping(source = "createdBy.id", target = "createdById")
    EventDto toDTO(Event event);

    @Mapping(source = "createdById", target = "createdBy.id")
    Event toEntity(EventDto eventDto);

    void updateEventFromDto(EventDto dto, @MappingTarget Event entity);
}
