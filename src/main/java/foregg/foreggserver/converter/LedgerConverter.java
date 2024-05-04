package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerSummaryDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerTotalResponseDTO;

import java.util.List;

public class LedgerConverter {

    public static Ledger toLedger(LedgerRequestDTO dto, User user) {
        return Ledger.builder()
                .ledgerType(dto.getLedgerType())
                .date(dto.getDate())
                .content(dto.getContent())
                .count(dto.getCount())
                .amount(dto.getAmount())
                .memo(dto.getMemo())
                .user(user).build();
    }

    public static LedgerResponseDTO toLedgerResponseDTO(Ledger ledger) {
        return LedgerResponseDTO.builder()
                .id(ledger.getId())
                .ledgerType(ledger.getLedgerType())
                .date(ledger.getDate())
                .amount(ledger.getAmount())
                .content(ledger.getContent())
                .count(ledger.getCount())
                .memo(ledger.getMemo()).build();
    }

    public static LedgerSummaryDTO toLedgerSummaryDTO(int totalExpense, int subsidy, int personal) {
        return LedgerSummaryDTO.builder()
                .totalExpense(totalExpense)
                .subsidy(subsidy)
                .personal(personal).build();
    }

    public static LedgerTotalResponseDTO toLedgerTotalResponseDTO(LedgerSummaryDTO ledgerSummaryDTO,
                                                                  List<LedgerResponseDTO> ledgerResponseDTOS) {
        return LedgerTotalResponseDTO.builder()
                .ledgerSummaryDTO(ledgerSummaryDTO)
                .ledgerResponseDTOS(ledgerResponseDTOS).build();
    }
}
