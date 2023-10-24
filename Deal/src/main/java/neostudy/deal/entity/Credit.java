package neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import neostudy.deal.dto.CreditStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "credit")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "application"})
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(0)
    private BigDecimal amount;

    @Column(nullable = false)
    @Min(0)
    private Integer term;

    @Column(name = "monthly_payment", nullable = false)
    @Min(0)
    private BigDecimal monthlyPayment;

    @Column(nullable = false)
    @Min(0)
    private BigDecimal rate;

    @Column(nullable = false)
    @Min(0)
    private BigDecimal psk;

    @Column(name = "insurance_enabled", nullable = false)
    private Boolean isInsuranceEnabled;

    @Column(name = "salary_client", nullable = false)
    private Boolean isSalaryClient;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_schedule", nullable = false)
    private List<PaymentScheduleElement> paymentSchedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status", nullable = false)
    CreditStatus creditStatus;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "credit")
    private Application application;
}
