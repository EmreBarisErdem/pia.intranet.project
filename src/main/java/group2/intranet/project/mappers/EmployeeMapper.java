package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.domain.dtos.OrgEmployeeDTO;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(target = "managerName", expression = "java(getManagerFullName(employee.getManager()))")
    EmployeeDTO toDto(Employee employee);

    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapToDepartment")
    @Mapping(target = "manager", source = "managerId", qualifiedByName = "mapToManager")
    Employee toEntity(EmployeeDTO dto);

    @Mapping(source = "firstName",           target = "firstName")
    @Mapping(source = "lastName",            target = "lastName")
    @Mapping(source = "jobTitle",            target = "jobTitle")
    @Mapping(source = "department.name",     target = "departmentName")
    @Mapping(source = "email",               target = "email")
    @Mapping(source = "phoneNumber",         target = "phoneNumber")
    @Mapping(target = "reports",             ignore = true)
    OrgEmployeeDTO toOrgDto(Employee employee);

    @Named("mapToDepartment")
    default Department mapToDepartment(Integer departmentId) {
        if (departmentId == null) return null;
        Department d = new Department();
        d.setId(departmentId);
        return d;
    }

    @Named("mapToManager")
    default Employee mapToManager(Integer managerId) {
        if (managerId == null) return null;
        Employee m = new Employee();
        m.setId(managerId);
        return m;
    }

    default String getManagerFullName(Employee manager) {
        if (manager == null) return null;
        String first = manager.getFirstName() != null ? manager.getFirstName() : "";
        String last = manager.getLastName() != null ? manager.getLastName() : "";
        return (first + " " + last).trim();
    }
}
