package group2.intranet.project.mappers;

import group2.intranet.project.domain.dtos.NewsDTO;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface NewsMapper {
    //@Mapping(target = "file", ignore = true)
    @Mapping(source = "departments", target = "departmentIds")
    @Mapping(source = "createdBy.id", target = "createdById")
    NewsDTO toDto(News news);

    //@Mapping(target = "cover_image", expression = "java(getBytes(newsDTO.getFile()))")
    @Mapping(source = "departmentIds", target = "departments")
    @Mapping(source = "createdById", target = "createdBy.id")
    News toEntity(NewsDTO newsDTO);

    void updateNewsFromDto(NewsDTO dto, @MappingTarget News entity);

//    default String encodeImage(byte[] imageData) {
//        return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
//    }
//
//    default byte[] decodeImage(String base64Image) {
//        return base64Image != null ? Base64.getDecoder().decode(base64Image) : null;
//    }

    default List<Department> mapDepartmentIdsToDepartments(List<Integer> ids) {
        if (ids == null) return null;
        List<Department> departments = new ArrayList<>();
        for (Integer id : ids) {
            departments.add(new Department(id, null, null, null, null, null, null, null, null, null));
        }
        return departments;
    }

//    default byte[] getBytes(MultipartFile file) {
//        try {
//            return file != null ? file.getBytes() : null;
//        } catch (IOException e) {
//
//            return null;
//        }
//    }

    default List<Integer> mapDepartmentsToIds(List<Department> departments) {
        if (departments == null) return null;
        return departments.stream()
                .map(Department::getId)
                .collect(Collectors.toList());
    }
}
