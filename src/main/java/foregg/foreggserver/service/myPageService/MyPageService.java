package foregg.foreggserver.service.myPageService;

import foregg.foreggserver.apiPayload.exception.handler.SurgeryHandler;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.myPageDTO.MyPageRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SurgeryRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_SURGERY;


@Transactional
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final SurgeryRepository surgeryRepository;
    private final UserQueryService userQueryService;

    public void modifySurgery(MyPageRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Surgery surgery = surgeryRepository.findByUser(user).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
        surgery.update(dto);
    }

}
