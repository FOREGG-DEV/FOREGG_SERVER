package foregg.foreggserver.service.ledgerService;

import foregg.foreggserver.apiPayload.exception.handler.LedgerHandler;
import foregg.foreggserver.converter.LedgerConverter;
import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.LedgerMemo;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.LedgerMemoRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.INVALID_DATE_RANGE;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_LEDGER;


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
    private final LedgerMemoRepository ledgerMemoRepository;

    public LedgerResponseDTO all() {
        List<Ledger> myLedgers = getHusbandAndWifeLedgers();
        List<Ledger> ledgers = new ArrayList<>();
        List<String> pastDays = DateUtil.getPast30Days();

        for (Ledger ledger : myLedgers) {
            if (pastDays.contains(ledger.getDate())) {
                ledgers.add(ledger);
            }
        }
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        return LedgerConverter.toLedgerResponseDTO(personalSum, subsidySum, null, total, toLedgerDetailDTO(ledgers));
    }

    //가계부 수정할 때 나오는 Detail
    public LedgerRequestDTO ledgerDetail(Long id) {
        User user = userQueryService.getUser();
        User spouse = userQueryService.returnSpouse();
        Ledger ledger = ledgerRepository.findById(id).orElseThrow(() -> new LedgerHandler(NOT_FOUND_MY_LEDGER));
        if (ledger.getUser() != user && ledger.getUser() != spouse) {
            throw new LedgerHandler(NOT_FOUND_MY_LEDGER);
        }
        return LedgerConverter.toLedgerRequestDTO(ledger);
    }

    public LedgerResponseDTO byMonth(String yearmonth) {
        List<Ledger> myLedgers = getHusbandAndWifeLedgers();
        List<Ledger> ledgers = new ArrayList<>();
        for(Ledger ledger : myLedgers) {
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
        List<Ledger> ledgers = filterLedgerByCount(getHusbandAndWifeLedgers(), count);

        //필요한게 personalSum, subsidySum = null, subsidyAvaliable, total, ledgerDetailResponseDTOS
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        List<LedgerResponseDTO.SubsidyAvailable> subsidyAvailable = subsidyQueryService.toSubsidyAvailable(count);
        LedgerResponseDTO dto = LedgerConverter.toLedgerResponseDTO(personalSum, null, subsidyAvailable, total, toLedgerDetailDTO(filterLedgerByCount(ledgers, count)));
        dto.setMemo(getLedgerMemo(count));
        return dto;
    }

    public LedgerResponseDTO byCondition(String from, String to) {
        List<Ledger> ledgers = getHusbandAndWifeLedgers();
        List<String> intervalDates = DateUtil.getIntervalDates(from, to);

        if (DateUtil.toLocalDate(from).isAfter(DateUtil.toLocalDate(to))) {
            throw new LedgerHandler(INVALID_DATE_RANGE);
        }

        // 사이값에 없는 가계부는 삭제
        for (Ledger ledger : ledgers) {
            if (!intervalDates.contains(ledger.getDate())) {
                ledgers.remove(ledger);
            }
        }
        int personalSum = expenditureQueryService.getPersonalExpenditure(ledgers);
        int subsidySum = expenditureQueryService.getSubsidyExpenditure(ledgers);
        int total = personalSum + subsidySum;
        return LedgerConverter.toLedgerResponseDTO(personalSum, subsidySum, null, total, toLedgerDetailDTO(ledgers));
    }

    public List<LedgerResponseDTO.LedgerDetailResponseDTO> toLedgerDetailDTO(List<Ledger> ledgers) {
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

    //현재 유저의 가계부와 배우자의 가계부를 합쳐서 반환하는 메서드
    public List<Ledger> getHusbandAndWifeLedgers() {
        List<Ledger> result = ledgerRepository.findByUser(userQueryService.getUser());
        User spouse = userQueryService.returnSpouse();
        if(spouse != null) {
            List<Ledger> spouseLedgers = ledgerRepository.findByUser(spouse);
            result.addAll(spouseLedgers);
        }
        return result;
    }

    private List<Ledger> filterLedgerByCount(List<Ledger> ledgers, int count) {
        return ledgers.stream()
                .filter(ledger -> ledger.getCount() == count)  // count와 같은 경우만 필터링
                .collect(Collectors.toList());
    }

    private String getLedgerMemo(int count) {
        LedgerMemo ledgerMemo = ledgerMemoRepository.findByUserAndCount(userQueryService.getUser(), count);
        if (ledgerMemo == null) {
            return null;
        }
        return ledgerMemo.getMemo();
    }

}
