package neostudy.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.dossier.dto.enums.Gender;
import neostudy.dossier.dto.enums.MaritalStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    @Schema(description = "User's firstname", type = "String", example = "Anna")
    private String firstName;

    @Schema(description = "User's lastname", type = "String", example = "Petrova")
    private String lastName;

    @Schema(description = "User's middle name", type = "String", example = "Sergeevna")
    private String middleName;

    @Schema(description = "User's birthdate. Should be before today", type = "String", example = "1990-01-01")
    private LocalDate birthdate;

    @Schema(description = "User's gender", type = "String", example = "FEMALE")
    private Gender gender;

    @Schema(description = "User's marital status", type = "String", example = "SINGLE")
    private MaritalStatus maritalStatus;

    @Schema(description = "Number of dependent's", type = "integer", format = "int32", example = "0")
    private Integer dependentAmount;

    @Schema(description = "User's account", type = "String", example = "0123456789")
    private String account;

    @Schema(description = "User's passport", type = "object")
    private PassportDTO passport;

    @Schema(description = "Description of employment status", type = "object")
    private EmploymentDTO employment;
}
