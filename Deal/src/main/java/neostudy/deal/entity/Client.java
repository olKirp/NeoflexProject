package neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import neostudy.deal.dto.Gender;
import neostudy.deal.dto.MaritalStatus;

import java.time.LocalDate;

@Data
@Entity
@Builder
@Table(name = "client",
        uniqueConstraints = { @UniqueConstraint(name = "unique_account", columnNames = { "account" }),
                @UniqueConstraint(name = "unique_email", columnNames = { "email" })})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "application"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    private String account;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
    private Application application;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id", referencedColumnName = "id", foreignKey=@ForeignKey(name = "fk_client_passport"))
    private Passport passport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_id", referencedColumnName = "id", foreignKey=@ForeignKey(name = "fk_client_employment"))
    private Employment employment;
}
