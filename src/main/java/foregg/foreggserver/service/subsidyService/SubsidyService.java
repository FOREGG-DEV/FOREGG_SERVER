package foregg.foreggserver.service.subsidyService;

import foregg.foreggserver.apiPayload.exception.handler.SubsidyHandler;
import foregg.foreggserver.converter.SubsidyConverter;
import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.SubsidyColorType;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SubsidyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.BUDGET_OVER;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_SUBSIDY;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SubsidyService {

    private final SubsidyRepository subsidyRepository;
    private final UserQueryService userQueryService;
    private final SubsidyQueryService subsidyQueryService;

    //남편이 추가해도 아내의 지원금으로 편입시키기
    public void createSubsidy(SubsidyRequestDTO dto) {
        User owner = userQueryService.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            owner = userQueryService.returnSpouse();
        }
        subsidyQueryService.subsidyExist(owner, dto.getNickname(), dto.getCount());
        int userSubsidyCount = subsidyRepository.countByUserAndCount(owner, dto.getCount());
        SubsidyColorType color = SubsidyColorType.values()[userSubsidyCount % SubsidyColorType.values().length];
        Subsidy subsidy = SubsidyConverter.toSubsidy(dto, owner, color);
        subsidyRepository.save(subsidy);
    }

    public void updateSubsidy(Long id ,SubsidyRequestDTO dto) {
        Subsidy subsidy = subsidyQueryService.getSubsidyByIdAndUser(id);
        subsidy.updateSubsidy(dto);
    }

    public void deleteSubsidy(Long id) {
        subsidyQueryService.getSubsidyByIdAndUser(id);
        subsidyRepository.deleteById(id);
    }

    //지원금 깎기
    public void deductSubsidy(List<Expenditure> expenditureList) {
        for (Expenditure expenditure : expenditureList) {
            if (expenditure.getName().equals("개인")) {
                continue;
            }
            Subsidy subsidy = subsidyQueryService.getSubsidyByUserCountName(expenditure.getLedger().getCount(), expenditure.getName());
            if (subsidy == null) {
                throw new SubsidyHandler(NOT_FOUND_MY_SUBSIDY);
            }
            if (subsidy.getAvailable() < expenditure.getAmount()) {
                throw new SubsidyHandler(BUDGET_OVER);
            }
            subsidy.updateExpenditure(expenditure.getAmount());
        }
    }

}
