package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import java.util.List;

public interface AnnouncementService {
    List<AnnouncementDTO> getAll();
    AnnouncementDTO getById(Integer id);
    AnnouncementDTO create(AnnouncementDTO dto);
    AnnouncementDTO update(Integer id, AnnouncementDTO dto);
    void delete(Integer id);
}
