package neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
@Table(name = "passport",
        uniqueConstraints = { @UniqueConstraint(name = "unique_passport_series_and_number", columnNames = { "series", "number" }) })
@ToString(exclude = { "client"})
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min=4, max = 4)
    @Column(nullable = false)
    private String series;

    @Length(min=6, max = 6)
    @Column(nullable = false)
    private String number;

    @Column(name = "issue_branch")
    private String issueBranch;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "passport")
    private Client client;
}
