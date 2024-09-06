package foregg.foreggserver.dto.expenditureDTO;

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
    private int amount;

}
