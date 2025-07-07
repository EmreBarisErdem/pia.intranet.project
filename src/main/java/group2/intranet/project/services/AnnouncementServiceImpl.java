package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.domain.entities.Announcement;
import group2.intranet.project.mappers.AnnouncementMapper;
import group2.intranet.project.repositories.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final AnnouncementMapper mapper;

    public AnnouncementServiceImpl(AnnouncementRepository repository, AnnouncementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<AnnouncementDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementDTO getById(Integer id) {
        Announcement announcement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
        return mapper.toDto(announcement);
    }

    @Override
    public AnnouncementDTO create(AnnouncementDTO dto) {
        Announcement announcement = mapper.toEntity(dto);
        return mapper.toDto(repository.save(announcement));
    }

    @Override
    public AnnouncementDTO update(Integer id, AnnouncementDTO dto) {

        Announcement existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));

        Announcement updated = mapper.toEntity(dto);

        updated.setId(id);

        return mapper.toDto(repository.save(updated));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
