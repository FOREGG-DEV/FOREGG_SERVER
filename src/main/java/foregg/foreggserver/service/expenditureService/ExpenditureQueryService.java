package foregg.foreggserver.service.expenditureService;

import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenditureQueryService {

    //사비 지출 총합
    public int getPersonalExpenditure(List<Ledger> ledgers) {
        return ledgers.stream()
                .flatMap(ledger -> ledger.getExpenditureList().stream())
                .filter(expenditure -> expenditure.getName().equals("개인"))
                .mapToInt(Expenditure::getAmount)
                .sum();
    }

    //지원금 지출 총합
    public int getSubsidyExpenditure(List<Ledger> ledgers) {
        return ledgers.stream()
                .flatMap(ledger -> ledger.getExpenditureList().stream())
                .filter(expenditure -> !expenditure.getName().equals("개인"))
                .mapToInt(Expenditure::getAmount)
                .sum();
    }



}
