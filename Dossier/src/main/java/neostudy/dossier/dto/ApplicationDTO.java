package neostudy.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.dossier.dto.enums.ApplicationStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {

    @Schema(description = "Application id", type = "integer", format = "int64", example = "1")
    private Long id;

    @Schema(description = "Application's status", type = "String", example = "PREAPPROVAL")
    private ApplicationStatus status;

    @Schema(description = "Application's creation date", type = "String", example = "2023-01-01")
    private LocalDate creationDate;

    @Schema(description = "Application's signing date", type = "String", example = "2023-01-01")
    private LocalDate signDate;

    @Schema(description = "Credit for application", type = "object")
    private CreditDTO credit;

    @Schema(description = "Client who created application", type = "object")
    private ClientDTO client;

    @Schema(description = "Simple electronic signature", type = "String", example = "1234", pattern = "[0-9]{4}")
    private String sesCode;
}
