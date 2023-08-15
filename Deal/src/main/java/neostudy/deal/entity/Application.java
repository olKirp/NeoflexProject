package neostudy.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.enums.ApplicationStatus;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDate;

import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "application")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history")
    private StatusHistory statusHistory;
}
