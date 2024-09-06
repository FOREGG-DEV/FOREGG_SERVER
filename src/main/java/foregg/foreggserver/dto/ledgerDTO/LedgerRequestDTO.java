package foregg.foreggserver.dto.ledgerDTO;

import foregg.foreggserver.dto.expenditureDTO.ExpenditureRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerRequestDTO {

    private String date;
    private int count;
    private String content;
    private String memo;
    private List<ExpenditureRequestDTO> expenditureRequestDTOList;

}
