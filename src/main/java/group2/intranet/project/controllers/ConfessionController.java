package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.ConfessionDto;
import group2.intranet.project.services.ConfessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/confessions")
@Validated
public class ConfessionController {

    private ConfessionService confessionService;

    public ConfessionController(ConfessionService confessionService) {
        this.confessionService = confessionService;
    }

    @GetMapping
    public ResponseEntity<List<ConfessionDto>> getAll() {
        try {
            List<ConfessionDto> confessionDtoList = confessionService.getAllConfessions();
            if (confessionDtoList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok().body(confessionDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfessionDto> getConfessionById(@PathVariable("id") @Min(1) Integer id) {
        try {
            ConfessionDto dto = confessionService.getConfessionById(id);
            if (dto == null) {
                return ResponseEntity.notFound().build(); // 404
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<ConfessionDto> createConfession(@RequestBody @Valid ConfessionDto dto) {
        try {
            ConfessionDto created = confessionService.createConfession(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ConfessionDto> updateConfession(@PathVariable("id") @Min(1) Integer id,
                                                              @RequestBody @Valid ConfessionDto dto) {

        try {
            ConfessionDto existing = confessionService.getConfessionById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build(); // 404
            }
            ConfessionDto updated = confessionService.updateConfession(id, dto);
            return ResponseEntity.ok(updated); // 200
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteConfession(@PathVariable("id") @Min(1) Integer id) {
        try {
            ConfessionDto existingConfession = confessionService.getConfessionById(id);
            if (existingConfession == null) {
                return ResponseEntity.notFound().build(); // 404
            }
            confessionService.deleteConfession(id);
            return ResponseEntity.ok("Confession deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
