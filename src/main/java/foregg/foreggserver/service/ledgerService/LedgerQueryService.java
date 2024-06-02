package foregg.foreggserver.service.ledgerService;

import foregg.foreggserver.apiPayload.exception.handler.LedgerHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.LedgerConverter;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.LedgerType;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerSummaryDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerTotalResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.LedgerRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class LedgerQueryService {

    private final LedgerRepository ledgerRepository;
    private final UserQueryService userQueryService;

    public LedgerTotalResponseDTO all() {
        List<LedgerResponseDTO> resultList = new ArrayList<>();
        List<Ledger> ls = new ArrayList<>();
        List<Ledger> ledgers = findLedgerByWife();

        List<String> pastDays = DateUtil.getPast30Days(DateUtil.formatLocalDateTime(LocalDate.now()));

        for (Ledger ledger : ledgers) {
            if (pastDays.contains(ledger.getDate())) {
                resultList.add(LedgerConverter.toLedgerResponseDTO(ledger));
                ls.add(ledger);
            }
        }
        return LedgerConverter.toLedgerTotalResponseDTO(calculateSummary(ls), resultList);
    }

    public LedgerTotalResponseDTO byCount(int count) {

        List<Ledger> ledgers = findLedgerByWife();
        List<LedgerResponseDTO> resultList = new ArrayList<>();
        List<Ledger> ls = new ArrayList<>();

        for (Ledger ledger : ledgers) {
            if (ledger.getCount() == count) {
                resultList.add(LedgerConverter.toLedgerResponseDTO(ledger));
                ls.add(ledger);
            }
        }
        return LedgerConverter.toLedgerTotalResponseDTO(calculateSummary(ls), resultList);
    }

    public LedgerTotalResponseDTO byMonth(String yearmonth) {

        List<Ledger> ledgers = findLedgerByWife();
        List<LedgerResponseDTO> resultList = new ArrayList<>();
        List<Ledger> ls = new ArrayList<>();

        for (Ledger ledger : ledgers) {
            if (DateUtil.extractSameYearmonth(yearmonth, ledger.getDate())) {
                resultList.add(LedgerConverter.toLedgerResponseDTO(ledger));
                ls.add(ledger);
            }
        }
        return LedgerConverter.toLedgerTotalResponseDTO(calculateSummary(ls), resultList);
    }

    public LedgerTotalResponseDTO byCondition(String from, String to) {

        List<Ledger> ledgers = findLedgerByWife();
        List<String> dates = DateUtil.getIntervalDates(from, to);
        List<LedgerResponseDTO> resultList = new ArrayList<>();
        List<Ledger> ls = new ArrayList<>();

        for (Ledger ledger : ledgers) {
            if (dates.contains(ledger.getDate())) {
                resultList.add(LedgerConverter.toLedgerResponseDTO(ledger));
                ls.add(ledger);
            }
        }
        return LedgerConverter.toLedgerTotalResponseDTO(calculateSummary(ls), resultList);
    }

    public LedgerResponseDTO detail(Long id) {
        Ledger ledger = ledgerRepository.findById(id).orElseThrow(() -> new LedgerHandler(LEDGER_NOT_FOUND));
        isMyLedger(ledger);
        return LedgerConverter.toLedgerResponseDTO(ledger);
    }

    private LedgerSummaryDTO calculateSummary(List<Ledger> ledgers) {
        int totalExpense = 0, subsidy = 0, personal = 0;
        for (Ledger ledger : ledgers) {
            if (ledger.getLedgerType() == LedgerType.SUBSIDY) {
                subsidy += ledger.getAmount();
            }else{
                personal += ledger.getAmount();
            }
            totalExpense += ledger.getAmount();
        }
        return LedgerConverter.toLedgerSummaryDTO(totalExpense, subsidy, personal);
    }

    private List<Ledger> findLedgerByWife() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            return ledgerRepository.findByUser(userQueryService.returnSpouse()).orElseThrow(() -> new UserHandler(SPOUSE_NOT_FOUND));
        }
        Optional<List<Ledger>> ledgers = ledgerRepository.findByUser(user);
        if (ledgers.isEmpty()) {
            return null;
        }
        return ledgers.get();
    }

    public void isMyLedger(Ledger ledger) {
        User ledgerOwner = ledger.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            if (!ledgerOwner.equals(userQueryService.returnSpouse())) {
                throw new LedgerHandler(NOT_MY_LEDGER);
            }
        }else{
            User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
            if (!ledgerOwner.equals(user)) {
                throw new LedgerHandler(NOT_MY_LEDGER);
            }
        }

    }

}
