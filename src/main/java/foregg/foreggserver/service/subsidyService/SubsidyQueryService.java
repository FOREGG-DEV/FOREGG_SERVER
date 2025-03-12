package foregg.foreggserver.service.subsidyService;

import foregg.foreggserver.apiPayload.exception.handler.SubsidyHandler;
import foregg.foreggserver.converter.SubsidyConverter;
import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
import foregg.foreggserver.dto.subsidyDTO.SubsidyResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SubsidyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_SUBSIDY;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.SUBSIDY_ALREADY_EXIST;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class SubsidyQueryService {

    private final SubsidyRepository subsidyRepository;
    private final UserQueryService userQueryService;


    public SubsidyRequestDTO detailSubsidy(Long id) {
        Subsidy subsidy = getSubsidyByIdAndUser(id);
        SubsidyRequestDTO result = SubsidyConverter.toSubsidyRequestDTO(subsidy);
        return result;
    }

    public Subsidy getSubsidyByIdAndUser(Long id) {
        User user = userQueryService.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            user = userQueryService.returnSpouse();
        }
        return subsidyRepository.findByIdAndUser(id, user).orElseThrow(() -> new SubsidyHandler(NOT_FOUND_MY_SUBSIDY));
    }

    public SubsidyResponseDTO getSubsidyByCount(int count) {
        User user = userQueryService.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            user = userQueryService.returnSpouse();
        }
        List<Subsidy> subsidies = subsidyRepository.findByUserAndCount(user, count);

        // 회차 날짜 로직을 구해서 null 값을 대체해야함
        return SubsidyConverter.toDetailResponseDTO(subsidies);
    }

    public Subsidy getSubsidyByUserCountName(int count, String name) {
        User user = userQueryService.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            user = userQueryService.returnSpouse();
        }
        return subsidyRepository.findByUserAndCountAndNickname(user, count, name);
    }

    public List<Subsidy> getSubsidyByUserAndCount(int count) {
        User user = userQueryService.getUser();
        List<Subsidy> subsidies = subsidyRepository.findByUserAndCount(user, count);
        return subsidies;
    }

    public List<LedgerResponseDTO.SubsidyAvailable> toSubsidyAvailable(int count) {
        User user = userQueryService.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            user = userQueryService.returnSpouse();
        }
        List<LedgerResponseDTO.SubsidyAvailable> result = new ArrayList<>();
        List<Subsidy> subsidies = subsidyRepository.findByUserAndCount(user, count);

        for (Subsidy subsidy : subsidies) {
            LedgerResponseDTO.SubsidyAvailable dto = LedgerResponseDTO.SubsidyAvailable.builder()
                    .nickname(subsidy.getNickname())
                    .color(subsidy.getColor())
                    .amount(subsidy.getAvailable())
                    .build();
            result.add(dto);
        }
        return result;
    }

    public void subsidyExist(User owner, String nickname, int count) {
        Subsidy subsidy = subsidyRepository.findByUserAndCountAndNickname(owner, count, nickname);
        if (subsidy != null) {
            throw new SubsidyHandler(SUBSIDY_ALREADY_EXIST);
        }
    }

    public void restoreSubsidy(List<Expenditure> expenditureList, int count) {
        expenditureList.removeIf(expenditure -> "개인".equals(expenditure.getName()));
        User user = userQueryService.getUser();
        for (Expenditure expenditure : expenditureList) {
            Subsidy subsidy = subsidyRepository.findByUserAndCountAndNickname(user, count, expenditure.getName());
            subsidy.restoreSubsidy(expenditure.getAmount());
        }

    }

}
