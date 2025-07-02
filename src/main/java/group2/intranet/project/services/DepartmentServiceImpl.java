package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.mappers.DepartmentMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }


    @Override
    public List<DepartmentDTO> getAllDepartments() {

        List<Department> deps = departmentRepository.findAll();

        return deps.stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO getDepartmentById(Integer id) {

        Department department = departmentRepository.findById(id).orElse(null);

        return departmentMapper.toDTO(department);
    }


}
