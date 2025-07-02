package group2.intranet.project.mappers;


import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.domain.entities.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentDTO toDTO(Department user);

    Department toEntity(DepartmentDTO userDTO);
}
