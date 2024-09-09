package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.expenditureDTO.ExpenditureRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;

import java.util.ArrayList;
import java.util.List;

public class ExpenditureConverter {

    public static List<Expenditure> toExpenditure(LedgerRequestDTO dto, Ledger ledger, User user) {
        List<ExpenditureRequestDTO> expenditureRequestDTOList = dto.getExpenditureRequestDTOList();
        List<Expenditure> expenditures = new ArrayList<>();

        for (ExpenditureRequestDTO eqd : expenditureRequestDTOList) {
            Expenditure expenditure = Expenditure.builder()
                    .name(eqd.getName())
                    .amount(eqd.getAmount())
                    .ledger(ledger)
                    .user(user)
                    .build();
            expenditures.add(expenditure);
        }
        return expenditures;
    }
}
