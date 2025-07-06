package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, DepartmentMapper.class})
public interface EventMapper {
    @Mapping(source = "departments", target = "departmentIds")
    @Mapping(source = "createdBy.id", target = "createdById")
    EventDto toDTO(Event event);

    @Mapping(source = "createdById", target = "createdBy.id")
    @Mapping(source = "departmentIds", target = "departments")
    Event toEntity(EventDto dto);

    void updateEventFromDto(EventDto dto, @MappingTarget Event entity);


    default List<Department> mapDepartmentIdsToDepartments(List<Integer> ids) {
        if (ids == null) return null;
        List<Department> departments = new ArrayList<>();
        for (Integer id : ids) {
            departments.add(new Department(id, null, null, null, null, null, null, null, null, null));
        }
        return departments;
    }

    default List<Integer> mapDepartmentsToIds(List<Department> departments) {
        if (departments == null) return null;
        return departments.stream()
                .map(Department::getId)
                .collect(Collectors.toList());
    }
}
