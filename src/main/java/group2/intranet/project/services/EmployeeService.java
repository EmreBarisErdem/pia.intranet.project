package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO getById(Long id);
    List<EmployeeDTO> getAll();

    void migratePasswords();
}
