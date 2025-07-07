package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.ConfessionDto;
import group2.intranet.project.domain.entities.Confession;
import group2.intranet.project.mappers.ConfessionMapper;
import group2.intranet.project.repositories.ConfessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfessionServiceImpl implements ConfessionService{

    private ConfessionRepository confessionRepository;
    private ConfessionMapper confessionMapper;

    public ConfessionServiceImpl(ConfessionMapper confessionMapper, ConfessionRepository confessionRepository) {
        this.confessionMapper = confessionMapper;
        this.confessionRepository = confessionRepository;
    }

    @Override
    public List<ConfessionDto> getAllConfessions() {
        List<Confession> confessions = confessionRepository.findAll();

        return confessions.stream()
                .map(confessionMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    public ConfessionDto getConfessionById(Integer id) {
        Confession confession = confessionRepository.findById(id).orElse(null);

        return confessionMapper.toDTO(confession);
    }

    @Override
    public ConfessionDto createConfession(ConfessionDto confessionDto) {
        Confession confession = confessionMapper.toEntity(confessionDto);

        return confessionMapper.toDTO(confessionRepository.save(confession));
    }

    @Override
    public ConfessionDto updateConfession(Integer id, ConfessionDto confessionDto) {

        Confession existingConfession = confessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Confession not found with id: " + id));

        Confession updatedConfession = confessionMapper.toEntity(confessionDto);

        updatedConfession.setId(Long.valueOf(id));

        return confessionMapper.toDTO(confessionRepository.save(updatedConfession));
    }

    @Override
    public void deleteConfession(Integer id) {
        confessionRepository.deleteById(id);
    }
}
