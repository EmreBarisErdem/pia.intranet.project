package group2.intranet.project.services;


import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.EmployeeMapper;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.services.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    public EmployeeServiceImpl(EmployeeRepository repo, EmployeeMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public EmployeeDTO getById(Long id) {
        Employee e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
        return mapper.toDto(e);
    }

    @Override
    public List<EmployeeDTO> getAll() {
        return repo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

}

