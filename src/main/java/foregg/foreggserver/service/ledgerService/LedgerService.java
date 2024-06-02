package foregg.foreggserver.service.ledgerService;

import foregg.foreggserver.apiPayload.exception.handler.LedgerHandler;
import foregg.foreggserver.converter.LedgerConverter;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.LedgerRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.LEDGER_NOT_FOUND;

@Transactional
@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserQueryService userQueryService;
    private final LedgerQueryService ledgerQueryService;
    private final FcmService fcmService;

    public void add(LedgerRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "새로운 가계부가 등록되었습니다", String.format("%s님이 가계부를 추가했습니다.", user.getNickname()), "ledger", null, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            ledgerRepository.save(LedgerConverter.toLedger(dto, spouse));
        }else{
            ledgerRepository.save(LedgerConverter.toLedger(dto, user));
        }
    }

    public void modify(Long id, LedgerRequestDTO dto) {
        Ledger ledger = ledgerRepository.findById(id).orElseThrow(() -> new LedgerHandler(LEDGER_NOT_FOUND));
        ledgerQueryService.isMyLedger(ledger);
        ledger.updateLedger(dto);
    }

    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id).orElseThrow(() -> new LedgerHandler(LEDGER_NOT_FOUND));
        ledgerQueryService.isMyLedger(ledger);
        ledgerRepository.delete(ledger);
    }

}
