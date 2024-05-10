package foregg.foreggserver.service.userService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.UserConverter;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.dto.userDTO.UserSpouseCodeResponseDTO;
import foregg.foreggserver.jwt.JwtTokenProvider;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.util.SpouseCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.USER_NEED_JOIN;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class UserQueryService {

    private final KakaoRequestService kakaoService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SpouseCodeGenerator spouseCodeGenerator;

    //클라이언트에게서 받은 access token을 이용해서 서버 측에 getUserInfo로 유저 정보를 받아 온다. 그 후에 db를 뒤져 있는 사용자 인지 아닌지 확인
    public Long isSignedUp(String token) {
        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(token);

        User user = userRepository.findByKeyCode(userInfo.getId().toString()).orElseThrow(() -> new UserHandler(USER_NEED_JOIN));
        return user.getId();
    }

    public UserResponseDTO isExist(String userKeycode) {
        Optional<User> foundUser = userRepository.findByKeyCode(userKeycode);
        String jwt = jwtTokenProvider.createToken(userKeycode);
        if (foundUser.isEmpty()) {
            throw new UserHandler(USER_NEED_JOIN);
        }
        return UserConverter.toUserResponseDTO(userKeycode, jwt);
    }

    public UserSpouseCodeResponseDTO getUserSpouseCode() {
        return UserSpouseCodeResponseDTO.builder().spouseCode(spouseCodeGenerator.generateRandomCode()).build();
    }

    public User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
    }
}
