package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.AnnouncementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            List<AnnouncementDTO> list = announcementService.getAll();
            if (list.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncementById(@PathVariable("id") @Min(1) Integer id) {
        try {
            AnnouncementDTO dto = announcementService.getById(id);

            if (dto == null) {
                return ResponseEntity.notFound().build(); // 404
            }

            return ResponseEntity.ok(dto);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@RequestBody @Valid AnnouncementDTO dto) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            Integer userId = loggedInEmployee.getId();

            dto.setCreatedById(Math.toIntExact(userId));

            AnnouncementDTO created = announcementService.create(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(@PathVariable("id") @Min(1) Integer id,
                                                  @RequestBody @Valid AnnouncementDTO dto) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            id = loggedInEmployee.getId();

            dto.setCreatedById(Math.toIntExact(id));

            AnnouncementDTO existing = announcementService.getById(id);

            if (existing == null) {
                return ResponseEntity.notFound().build(); // 404
            }

            AnnouncementDTO updated = announcementService.update(id, dto);

            return ResponseEntity.ok(updated); // 200
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable("id") @Min(1) Integer id) {
        try {
            AnnouncementDTO existing = announcementService.getById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build(); // 404
            }
            announcementService.delete(id);
            return ResponseEntity.ok("Announcement deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
