package foregg.foreggserver.service;

import foregg.foreggserver.apiPayload.code.status.ErrorStatus;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final KakaoRequestService kakaoService;
    private final UserRepository userRepository;

    //클라이언트에게서 받은 access token을 이용해서 서버 측에 getUserInfo로 유저 정보를 받아 온다. 그 후에 db를 뒤져 있는 사용자 인지 아닌지 확인
    @Transactional(readOnly = true)
    public Long isSignedUp(String token) {
        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(token);
        User user = userRepository.findByKeyCode(userInfo.getId().toString()).orElseThrow(() -> new UserHandler(ErrorStatus.USER_NEED_JOIN));
        return user.getId();
    }
}
