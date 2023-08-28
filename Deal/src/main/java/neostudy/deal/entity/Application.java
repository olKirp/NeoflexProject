package neostudy.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.enums.ApplicationStatus;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "application")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(exclude = { "client"})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false, foreignKey=@ForeignKey(name = "fk_application_client"))
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer")
    private LoanOfferDTO appliedOffer;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "credit_id", referencedColumnName = "id", foreignKey=@ForeignKey(name = "fk_application_credit"))
    private Credit credit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history")
    private StatusHistory statusHistory;

    @Column(name = "ses_code")
    private String sesCode;
}
