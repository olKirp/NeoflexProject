package neostudy.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportDTO {

    @Schema(description = "User's passport series", type = "String", example = "1111")
    private String series;

    @Schema(description = "User's passport number", type = "String", example = "222222")
    private String number;

    @Schema(description = "Passport issue branch", type = "String", example = "Department of Internal Affairs in Moscow")
    private String issueBranch;

    @Schema(description = "Passport issue date. Should be before today", type = "String", example = "2001-01-01")
    private LocalDate issueDate;
}
