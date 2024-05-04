package foregg.foreggserver.dto.ledgerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerSummaryDTO {

    private int totalExpense;
    private int subsidy;
    private int personal;

}
