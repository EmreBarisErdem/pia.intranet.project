package group2.intranet.project.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "confessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Confession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @Column(name = "nickname", nullable = true)
    private String nickname;

    @Size(max = 512)
    @Column(name = "confession", nullable = false)
    private String confession;

    @Column(name = "age_gap" , nullable = false)
    private String ageGap;

    @Column(name = "department" , nullable = false)
    private String department;

    @Column(name = "time_of_confession")
    private LocalDateTime timeOfConfession = LocalDateTime.now();

//    @ManyToOne
//    @JoinColumn(name = "department_id", nullable = false)
//    private Department department;

    @Column(name = "terms_accepted")
    @AssertTrue(message = "Terms must be accepted")
    private boolean termsAccepted;
}
