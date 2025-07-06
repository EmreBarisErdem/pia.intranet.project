package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.services.AnnouncementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
@Validated
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getAll() {
        List<AnnouncementDTO> list = announcementService.getAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncementById(@PathVariable("id") @Min(1) Integer id) {
        AnnouncementDTO dto = announcementService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@RequestBody @Valid AnnouncementDTO dto) {
        AnnouncementDTO created = announcementService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(@PathVariable("id") @Min(1) Integer id,
                                                  @RequestBody @Valid AnnouncementDTO dto) {
        AnnouncementDTO existing = announcementService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        AnnouncementDTO updated = announcementService.update(id, dto);
        return ResponseEntity.ok(updated); // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable("id") @Min(1) Integer id) {
        AnnouncementDTO existing = announcementService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        announcementService.delete(id);
        return ResponseEntity.ok("Announcement deleted successfully");
    }
}
