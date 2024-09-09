package foregg.foreggserver.service.ledgerService;

import foregg.foreggserver.apiPayload.exception.handler.LedgerHandler;
import foregg.foreggserver.apiPayload.exception.handler.SurgeryHandler;
import foregg.foreggserver.converter.ExpenditureConverter;
import foregg.foreggserver.converter.LedgerConverter;
import foregg.foreggserver.converter.SurgeryConverter;
import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.LedgerRepository;
import foregg.foreggserver.repository.SurgeryRepository;
import foregg.foreggserver.service.expenditureService.ExpenditureService;
import foregg.foreggserver.service.myPageService.MyPageService;
import foregg.foreggserver.service.subsidyService.SubsidyQueryService;
import foregg.foreggserver.service.subsidyService.SubsidyService;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.LEDGER_NOT_FOUND;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_SURGERY;


@Service
@Transactional
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserQueryService userQueryService;
    private final ExpenditureService expenditureService;
    private final SubsidyService subsidyService;
    private final SurgeryRepository surgeryRepository;
    private final MyPageService myPageService;
    private final SubsidyQueryService subsidyQueryService;

    public void writeLedger(LedgerRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());

        //가계부 저장
        Ledger ledger = LedgerConverter.toLedger(dto, user);
        ledgerRepository.save(ledger);

        //지출 저장
        List<Expenditure> expenditures = ExpenditureConverter.toExpenditure(dto, ledger, user);

        //지원금 깎기
        subsidyService.deductSubsidy(expenditures);

        //각 지출들에 대해서 지원금을 찾아서 넣어 주고 저장
        expenditureService.saveExpenditures(expenditures);
    }

    public void deleteLedger(Long id) {
        //단순히 삭제하는게 아니라 지원금을 원상복구 해야됨
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Ledger ledger = ledgerRepository.findByIdAndUser(id, user).orElseThrow(() -> new LedgerHandler(LEDGER_NOT_FOUND));
        List<Expenditure> expenditureList = ledger.getExpenditureList();
        subsidyQueryService.restoreSubsidy(expenditureList, ledger.getCount());
        ledgerRepository.delete(ledger);
    }

    public void modifyLedger(LedgerRequestDTO dto, Long id) {
        deleteLedger(id);
        writeLedger(dto);
    }

    public void createCount() {
        User currentUser = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Surgery surgery = surgeryRepository.findByUser(currentUser).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
        Surgery updateSurgery = Surgery.builder()
                .surgeryType(surgery.getSurgeryType())
                .count(surgery.getCount() + 1)
                .startAt(surgery.getStartAt())
                .build();
        myPageService.modifySurgery(SurgeryConverter.toMyPageRequestDTO(updateSurgery));
    }

}
