package foregg.foreggserver.service.ledgerService;

import foregg.foreggserver.converter.LedgerConverter;
import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.LedgerRepository;
import foregg.foreggserver.service.expenditureService.ExpenditureQueryService;
import foregg.foreggserver.service.myPageService.MyPageQueryService;
import foregg.foreggserver.service.subsidyService.SubsidyQueryService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerQueryService {

    private final UserQueryService userQueryService;
    private final LedgerRepository ledgerRepository;
    private final ExpenditureQueryService expenditureQueryService;
    private final MyPageQueryService myPageQueryService;
    private final SubsidyQueryService subsidyQueryService;

    public LedgerResponseDTO all() {
        List<Ledger> foundLedgers = ledgerRepository.findByUser(userQueryService.getUser(SecurityUtil.getCurrentUser()));
        List<Ledger> ledgers = new ArrayList<>();
        List<String> pastDays = DateUtil.getPast30Days();

        for (Ledger ledger : foundLedgers) {
            if (pastDays.contains(ledger.getDate())) {
                ledgers.add(ledger);
            }
        }
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        return LedgerConverter.toLedgerResponseDTO(personalSum, subsidySum, null, total, toLedgerDetailDTO(ledgers));
    }

    public LedgerResponseDTO byMonth(String yearmonth) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Ledger> foundLedgers = ledgerRepository.findByUser(user);
        List<Ledger> ledgers = new ArrayList<>();
        for(Ledger ledger : foundLedgers) {
            String yearAndMonth = DateUtil.getYearAndMonth(ledger.getDate());
            if (yearAndMonth.equals(yearmonth)) {
                ledgers.add(ledger);
            }
        }
        //필요한게 personalSum, subsidySum, subsidyAvaliable = null, total, ledgerDetailResponseDTOS
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        return LedgerConverter.toLedgerResponseDTO(personalSum, subsidySum, null, total, toLedgerDetailDTO(ledgers));
    }

    public LedgerResponseDTO byCountInit() {
        int count = myPageQueryService.getInformation().getCount();
        return byCount(count);
    }

    public LedgerResponseDTO byCount(int count) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Ledger> ledgers = ledgerRepository.findByUserAndCount(user, count);

        //필요한게 personalSum, subsidySum = null, subsidyAvaliable, total, ledgerDetailResponseDTOS
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        List<LedgerResponseDTO.SubsidyAvailable> subsidyAvailable = subsidyQueryService.toSubsidyAvailable(ledgers, count);
        return LedgerConverter.toLedgerResponseDTO(personalSum, null, subsidyAvailable, total, toLedgerDetailDTO(ledgers));
    }


    public List<LedgerResponseDTO.LedgerDetailResponseDTO>  toLedgerDetailDTO(List<Ledger> ledgers) {
        List<LedgerResponseDTO.LedgerDetailResponseDTO> result = new ArrayList<>();
        for (Ledger ledger : ledgers) {
            List<Expenditure> expenditureList = ledger.getExpenditureList();
            for (Expenditure expenditure : expenditureList) {
                LedgerResponseDTO.LedgerDetailResponseDTO dto = LedgerConverter.toLedgerDetailDTO(expenditure);
                result.add(dto);
            }
        }
        return result;
    }

}
