package neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import neostudy.deal.dto.enums.EmploymentStatus;
import neostudy.deal.dto.enums.EmploymentPosition;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "employment",
        uniqueConstraints = { @UniqueConstraint(name = "unique_inn", columnNames = { "employer_inn" }) })
@ToString(exclude = { "client"})
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=12, max = 12)
    @Column(name = "employer_inn")
    private String INN;

    @Min(0)
    private BigDecimal salary;

    @Min(0)
    private Integer workExperienceTotal;

    @Min(0)
    private Integer workExperienceCurrent;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "employment", fetch = FetchType.LAZY)
    private Client client;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private EmploymentPosition employmentPosition;
}
