package group2.intranet.project.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String location;

    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //Relationships

    //will be added after creating all the entities....

    /*
        @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees;

    @ManyToMany
    @JoinTable(
        name = "department_event",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

    @ManyToMany
    @JoinTable(
        name = "department_announcement",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "announcement_id")
    )
    private List<Announcement> announcements;

    @ManyToMany
    @JoinTable(
        name = "department_news",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> newsList;

    @ManyToMany
    @JoinTable(
        name = "department_document",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documents;
     */

}
