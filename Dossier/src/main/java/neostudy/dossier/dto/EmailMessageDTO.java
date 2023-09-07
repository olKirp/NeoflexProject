package neostudy.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.dossier.dto.enums.Theme;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDTO {

    @Schema(description = "User's email address", type = "String", example = "example@mail.com")
    private String address;

    @Schema(description = "Message theme (kafka topic)", type = "String", example = "SEND_SES")
    private Theme theme;

    @Schema(description = "Application's id", type = "integer", format = "int64", example = "1")
    private Long applicationId;
}
