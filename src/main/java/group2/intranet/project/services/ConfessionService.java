package group2.intranet.project.services;


import group2.intranet.project.domain.dtos.ConfessionDto;

import java.util.List;

public interface ConfessionService {
    List<ConfessionDto> getAllConfessions();

    ConfessionDto getConfessionById(Integer id);

    ConfessionDto createConfession(ConfessionDto confessionDto);

    ConfessionDto updateConfession(Integer id ,ConfessionDto confessionDto);

    void deleteConfession(Integer id);
}
