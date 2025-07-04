package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.domain.dtos.OrgEmployeeDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.EmployeeMapper;
import group2.intranet.project.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgChartService {
    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    public OrgChartService(EmployeeRepository repo,
                           EmployeeMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }


    public List<OrgEmployeeDTO> getOrgTree() {
        List<Employee> all = repo.findAllWithManager();
        return buildTree(all);
    }


    public List<OrgEmployeeDTO> getOrgTreeById(Integer id) {
        // 1) Load all employees + their manager
        List<Employee> all = repo.findAllWithManager();

        // 2) Build map: managerId -> list of direct reports
        Map<Integer, List<Employee>> childrenMap = all.stream()
                .filter(e -> e.getManager() != null)
                .collect(Collectors.groupingBy(e -> e.getManager().getId()));

        // 3) Locate the target
        Employee target = all.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + id));

        // 4) Build full descendants subtree for target
        OrgEmployeeDTO targetWithDesc = buildDescendants(target, childrenMap);

        // 5) Collect manager chain
        List<Employee> ancestors = new ArrayList<>();
        Employee cursor = target;
        while (cursor.getManager() != null) {
            cursor = cursor.getManager();
            ancestors.add(cursor);
        }
        Collections.reverse(ancestors);

        // 6) If target is root (no manager), return all roots as list
        if (ancestors.isEmpty()) {
            List<Employee> roots = all.stream()
                    .filter(e -> e.getManager() == null)
                    .collect(Collectors.toList());

            return roots.stream()
                    .map(e -> {
                        if (e.getId().equals(id)) {
                            return targetWithDesc;
                        } else {
                            OrgEmployeeDTO dto = mapper.toOrgDto(e);
                            dto.setReports(new ArrayList<>()); // shallow
                            return dto;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // 7) Otherwise, build peers at target's level
        Employee directManager = ancestors.get(ancestors.size() - 1);
        List<Employee> peers = childrenMap.getOrDefault(directManager.getId(), Collections.emptyList());

        List<OrgEmployeeDTO> peerDtos = peers.stream()
                .map(e -> {
                    if (e.getId().equals(id)) {
                        return targetWithDesc;
                    } else {
                        OrgEmployeeDTO dto = mapper.toOrgDto(e);
                        dto.setReports(new ArrayList<>()); // shallow
                        return dto;
                    }
                })
                .collect(Collectors.toList());

        // 8) Wrap peers under their manager node
        OrgEmployeeDTO managerDto = mapper.toOrgDto(directManager);
        managerDto.setReports(peerDtos);

        // 9) Wrap remaining ancestors
        OrgEmployeeDTO current = managerDto;
        for (int i = ancestors.size() - 2; i >= 0; i--) {
            OrgEmployeeDTO ancDto = mapper.toOrgDto(ancestors.get(i));
            ancDto.setReports(Collections.singletonList(current));
            current = ancDto;
        }

        // 10) Return as singleton list
        return Collections.singletonList(current);
    }

    /**
     * Recursively builds an OrgEmployeeDTO for the given employee,
     * including all descendants at any depth.
     */
    private OrgEmployeeDTO buildDescendants(Employee e, Map<Integer, List<Employee>> childrenMap) {
        OrgEmployeeDTO dto = mapper.toOrgDto(e);
        dto.setReports(new ArrayList<>());

        List<Employee> children = childrenMap.get(e.getId());
        if (children != null) {
            for (Employee child : children) {
                dto.getReports().add(buildDescendants(child, childrenMap));
            }
        }

        return dto;
    }

    private List<OrgEmployeeDTO> buildTree(List<Employee> all) {
        Map<Integer, OrgEmployeeDTO> map = all.stream()
                .collect(Collectors.toMap(
                        Employee::getId,
                        e -> {
                            OrgEmployeeDTO dto = mapper.toOrgDto(e);
                            dto.setReports(new ArrayList<>());
                            return dto;
                        }
                ));
        List<OrgEmployeeDTO> roots = new ArrayList<>();
        for (Employee e : all) {
            if (e.getManager() != null) {
                map.get(e.getManager().getId())
                        .getReports()
                        .add(map.get(e.getId()));
            } else {
                roots.add(map.get(e.getId()));
            }
        }
        return roots;
    }
}
