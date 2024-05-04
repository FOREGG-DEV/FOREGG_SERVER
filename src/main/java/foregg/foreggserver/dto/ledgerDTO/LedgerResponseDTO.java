package foregg.foreggserver.dto.ledgerDTO;

import foregg.foreggserver.domain.enums.LedgerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerResponseDTO {

    private Long id;
    private LedgerType ledgerType;
    private String date;
    private String content;
    private int amount;
    private int count;
    private String memo;

}
