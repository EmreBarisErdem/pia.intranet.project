package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.domain.entities.Announcement;
import group2.intranet.project.domain.entities.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(expression = "java(announcement.getCreatedBy().getFirstName() + \" \" + announcement.getCreatedBy().getLastName())", target = "createdByName")
    //@Mapping(source = "isPublic", target = "publicAnnouncement")
    AnnouncementDTO toDto(Announcement announcement);

    @Mapping(target = "createdBy", source = "createdById", qualifiedByName = "mapToEmployee")
    //@Mapping(source = "publicAnnouncement", target = "isPublic")
    Announcement toEntity(AnnouncementDTO dto);

    @Named("mapToEmployee")
    default Employee mapToEmployee(Integer employeeId) {
        if (employeeId == null) return null;
        Employee e = new Employee();
        e.setId(employeeId);
        return e;
    }
}
