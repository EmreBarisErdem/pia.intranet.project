package group2.intranet.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    @Column(name = "document_type", length = 30)
    private String documentType;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    //Relationships

    // ManyToOne - uploadedBy (Employee)
    @ManyToOne
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id", foreignKey = @ForeignKey(name = "uploaded_documents"))
    private Employee uploadedBy;

    // ManyToMany - departments
    @ManyToMany
    @JoinTable(
            name = "department_document",
            joinColumns = @JoinColumn(name = "document_id", foreignKey = @ForeignKey(name = "document_departments")),
            inverseJoinColumns = @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "department_documents"))
    )
    private List<Department> departments;



}
