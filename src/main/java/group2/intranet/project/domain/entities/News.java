package group2.intranet.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    @Column(name = "news_type")
    private String newsType;

    @Column(name = "cover_image", columnDefinition = "BYTEA")
    private byte[] cover_image;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    //Relationships

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Employee createdBy;

    @ManyToMany
    @JoinTable(
            name = "department_news",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> departments;
}
