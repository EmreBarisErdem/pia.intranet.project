package group2.intranet.project.mappers;


import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.domain.entities.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO toDTO(Department department);

    Department toEntity(DepartmentDTO departmentDTO);

    // ID'den Department entity üret
    default Department fromId(Integer id) {
        if (id == null) return null;
        Department department = new Department();
        department.setId(id);
        return department;
    }

    // Department entity'den ID çek
    default Integer toId(Department department) {
        return department != null ? department.getId() : null;
    }
}
