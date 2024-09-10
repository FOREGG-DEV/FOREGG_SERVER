package foregg.foreggserver.dto.expenditureDTO;

import foregg.foreggserver.domain.enums.SubsidyColorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureRequestDTO {

    private String name;
    private SubsidyColorType color;
    private int amount;

}
