package group2.intranet.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    @Column(name = "event_type", length = 30)
    private String eventType;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    private String status;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private String location;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

//    // Relationship
//    @ManyToOne
//    @JoinColumn(name = "created_by", nullable = false)
//    private Employee createdBy;
//
//    @ManyToMany
//    @JoinTable(
//            name = "department_event",
//            joinColumns = @JoinColumn(name = "department_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id")
//    )
//    private List<Department> events;
}
