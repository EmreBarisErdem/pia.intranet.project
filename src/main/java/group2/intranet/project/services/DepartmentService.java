package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.DepartmentDTO;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDTO> getAllDepartments();

    DepartmentDTO getDepartmentById(Integer id);

}

