package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.SubsidyColorType;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;

import java.util.List;

public class LedgerConverter {

    public static Ledger toLedger(LedgerRequestDTO dto, User user) {
        return Ledger.builder()
                .date(dto.getDate())
                .count(dto.getCount())
                .content(dto.getContent())
                .memo(dto.getMemo())
                .user(user)
                .build();
    }

    public static LedgerResponseDTO toLedgerResponseDTO(int personalSum,
                                                        Integer subsidySum,
                                                        List<LedgerResponseDTO.SubsidyAvailable> subsidyAvailable,
                                                        int total,
                                                        List<LedgerResponseDTO.LedgerDetailResponseDTO> detailResponseDTOS) {
        return LedgerResponseDTO.builder()
                .personalSum(personalSum)
                .subsidySum(subsidySum)
                .subsidyAvailable(subsidyAvailable)
                .total(total)
                .ledgerDetailResponseDTOS(detailResponseDTOS)
                .build();
    }


    public static LedgerResponseDTO.LedgerDetailResponseDTO toLedgerDetailDTO(Expenditure expenditure) {
        SubsidyColorType color = SubsidyColorType.RED;
        if (!expenditure.getName().equals("개인")) {
            color = expenditure.getSubsidy().getColor();
        }

        return LedgerResponseDTO.LedgerDetailResponseDTO.builder()
                .id(expenditure.getId())
                .date(expenditure.getLedger().getDate())
                .count(expenditure.getLedger().getCount())
                .color(color)
                .name(expenditure.getName())
                .content(expenditure.getLedger().getContent())
                .amount(expenditure.getAmount())
                .build();
    }



}
