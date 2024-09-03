package foregg.foreggserver.service.subsidyService;

import foregg.foreggserver.converter.SubsidyConverter;
import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.SubsidyColorType;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SubsidyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class SubsidyService {

    private final SubsidyRepository subsidyRepository;
    private final UserQueryService userQueryService;
    private final SubsidyQueryService subsidyQueryService;

    public void createSubsidy(SubsidyRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        int userSubsidyCount = subsidyRepository.countByUser(user);
        SubsidyColorType color = SubsidyColorType.values()[userSubsidyCount % SubsidyColorType.values().length];
        Subsidy subsidy = SubsidyConverter.toSubsidy(dto, user,color);
        subsidyRepository.save(subsidy);
    }

    public void updateSubsidy(Long id,SubsidyRequestDTO dto) {
        Subsidy subsidy = subsidyQueryService.getSubsidyById(id);
        subsidy.updateSubsidy(dto);
    }

    public void deleteSubsidy(Long id) {
        subsidyQueryService.getSubsidyById(id);
        subsidyRepository.deleteById(id);
    }



}
