package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.domain.dtos.OrgEmployeeDTO;
import group2.intranet.project.services.OrgChartService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chart")
public class OrgChartController {
    private final OrgChartService orgChartService;

    public OrgChartController(OrgChartService orgChartService) {
        this.orgChartService = orgChartService;
    }

    @GetMapping
    public List<OrgEmployeeDTO> getChart() {
        return orgChartService.getOrgTree();
    }

    @GetMapping("{id}")
    public List<OrgEmployeeDTO> getChartById(@PathVariable Integer id) {
        return orgChartService.getOrgTreeById(id);
    }
}
