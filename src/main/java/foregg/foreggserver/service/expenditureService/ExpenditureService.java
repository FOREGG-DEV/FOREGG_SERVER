package foregg.foreggserver.service.expenditureService;

import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.repository.ExpenditureRepository;
import foregg.foreggserver.service.subsidyService.SubsidyQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final SubsidyQueryService subsidyQueryService;

    public void saveExpenditures(List<Expenditure> expenditureList) {
        for (Expenditure expenditure : expenditureList) {
            Subsidy subsidy = subsidyQueryService.getSubsidyByUserCountName(expenditure.getLedger().getCount(), expenditure.getName());
            expenditure.setSubsidy(subsidy);
            expenditureRepository.save(expenditure);
        }
    }

}
