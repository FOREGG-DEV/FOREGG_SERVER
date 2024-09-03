package foregg.foreggserver.service.subsidyService;

import foregg.foreggserver.apiPayload.exception.handler.SubsidyHandler;
import foregg.foreggserver.converter.SubsidyConverter;
import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.subsidyDTO.SubsidyResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SubsidyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_SUBSIDY;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SubsidyQueryService {

    private final SubsidyRepository subsidyRepository;
    private final UserQueryService userQueryService;

    public Subsidy getSubsidyById(Long id) {
        Subsidy subsidy = subsidyRepository.findById(id).orElseThrow(() -> new SubsidyHandler(NOT_FOUND_MY_SUBSIDY));
        return subsidy;
    }

    public SubsidyResponseDTO getSubsidyByCount(int count) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Subsidy> subsidies = subsidyRepository.findByUserAndCount(user, count);
        // 회차 날짜 로직을 구해서 null 값을 대체해야함
        return SubsidyConverter.toDetailResponseDTO(null, subsidies);
    }
}
