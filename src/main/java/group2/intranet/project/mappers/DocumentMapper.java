package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.DocumentDto;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Document;
import group2.intranet.project.domain.entities.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "departmentIds", expression = "java(mapDepartmentIds(document.getDepartments()))")
    DocumentDto toDto(Document document);

    @Mapping(target = "fileData", expression = "java(getBytes(document.getFile()))")
    @Mapping(target = "uploadedBy", expression = "java(mapEmployee(document.getUploadedById()))")
    @Mapping(target = "departments", expression = "java(mapDepartments(document.getDepartmentIds()))")
    Document toEntity(DocumentDto document);

    default byte[] getBytes(MultipartFile file) {
        try {
            return file != null ? file.getBytes() : null;
        } catch (IOException e) {
            // log atılabilir ya da özel hata yönetimi yapılabilir
            return null;
        }
    }

    // Bu metodlar Service içinde inject edilen repo'larla override edilebilir:
    default Employee mapEmployee(Integer id) {
        Employee emp = new Employee();
        emp.setId(id);
        return emp;
    }

    default List<Department> mapDepartments(List<Integer> ids) {
        return ids.stream().map(id -> {
            Department d = new Department();
            d.setId(id);
            return d;
        }).collect(Collectors.toList());
    }

    default List<Integer> mapDepartmentIds(List<Department> departments) {
        return departments.stream().map(Department::getId).collect(Collectors.toList());
    }
}
