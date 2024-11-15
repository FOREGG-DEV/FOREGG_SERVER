package foregg.foreggserver.service.userService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserSpouseCodeResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.util.SpouseCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class UserQueryService {

    private final KakaoRequestService kakaoService;
    private final UserRepository userRepository;
    private final SpouseCodeGenerator spouseCodeGenerator;

    //클라이언트에게서 받은 access token을 이용해서 서버 측에 getUserInfo로 유저 정보를 받아 온다. 그 후에 db를 뒤져 있는 사용자 인지 아닌지 확인
    public Long isSignedUp(String token) {
        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(token);

        User user = userRepository.findByKeyCode(userInfo.getId().toString()).orElseThrow(() -> new UserHandler(USER_NEED_JOIN));
        return user.getId();
    }

    public void isExist(String userKeycode) {
        Optional<User> foundUser = userRepository.findByKeyCode(userKeycode);
        if (foundUser.isEmpty()) {
            throw new UserHandler(USER_NEED_JOIN);
        }
    }

    public UserSpouseCodeResponseDTO getUserSpouseCode() {
        return UserSpouseCodeResponseDTO.builder().spouseCode(spouseCodeGenerator.generateRandomCode()).build();
    }

    public User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
    }

    //현재 유저가 남편이면 와이프 반환, 남편이 아니라면 그냥 현재 유저 반환 -> 그냥 와이프 반환하는 메서드
    public User returnWifeOrHusband() {
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            Long spouseId = getUser(SecurityUtil.getCurrentUser()).getSpouseId();
            return userRepository.findById(spouseId).orElseThrow(() -> new UserHandler(SPOUSE_NOT_FOUND));
        }else{
            return getUser(SecurityUtil.getCurrentUser());
        }
    }

    //배우자 반환
    public User returnSpouse() {
        User user = getUser(SecurityUtil.getCurrentUser());
        Long spouseId = user.getSpouseId();
        if (spouseId == null) {
            return null;
        }
        Optional<User> spouse = userRepository.findById(spouseId);
        if (spouse.isEmpty()) {
            return null;
        }
        return spouse.get();
    }

    public boolean challengeNameExist(String challengeName) {
        User user = userRepository.findByChallengeName(challengeName);
        if (user == null) {
            return false;
        }
        return true;
    }

    public String getChallengeName() {
        User user = this.getUser(SecurityUtil.getCurrentUser());
        return user.getChallengeName();
    }

}
