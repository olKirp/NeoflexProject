package neostudy.deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleElement {

    @Schema(description = "Number of loan payment", type = "integer", format = "int32", example = "1")
    Integer number;

    @Schema(description = "Date of loan payment", type = "number", example = "2023-11-11")
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate date;

    @Schema(description = "Total amount of payment", type = "number", example = "15000")
    BigDecimal totalPayment;

    @Schema(description = "Amount of interest payment", type = "number", example = "5000")
    BigDecimal interestPayment;

    @Schema(description = "Amount of debt payment", type = "number", example = "10000")
    BigDecimal debtPayment;

    @Schema(description = "Remaining debt after payment ", type = "number", example = "80000")
    BigDecimal remainingDebt;
}
