package foregg.foreggserver.service.userService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.SurgeryConverter;
import foregg.foreggserver.converter.UserConverter;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NavigationType;
import foregg.foreggserver.dto.fcmDTO.FcmRenewalRequest;
import foregg.foreggserver.dto.userDTO.LogoutWithdrawalResponseDTO;
import foregg.foreggserver.dto.userDTO.UserHusbandJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.jwt.JwtTokenProvider;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SurgeryRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.notificationService.NotificationService;
import foregg.foreggserver.service.redisService.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SurgeryRepository surgeryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final KakaoRequestService kakaoRequestService;
    private final RedisService redisService;
    private final UserQueryService userQueryService;
    private final FcmService fcmService;

    public UserResponseDTO login(String userPk) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        User user = userQueryService.getUser();
        user.setLastConnect(LocalDateTime.now());

        String accessToken = jwtTokenProvider.createToken(userPk);
        String refreshToken = jwtTokenProvider.createRefresh(userPk);

        //notificationService.scheduleAwakeUser(user);

        return UserResponseDTO.builder()
                .keyCode(userPk)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // DB에 유저 정보 저장하고, JWT 토큰 만들어서 보내주기
    public UserResponseDTO join(String token, UserJoinRequestDTO dto) {
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(token);
        Long userId = userInfo.getId();
        Optional<User> foundUser = userRepository.findByKeyCode(userId.toString());
        if (foundUser.isPresent()) {
            throw new UserHandler(ALREADY_JOIN);
        }

        String accessToken = jwtTokenProvider.createToken(userId.toString());
        String refreshToken = jwtTokenProvider.createRefresh(userId.toString());

       // String keyCode = jwtTokenProvider.getUserPk(accessToken);

        Surgery surgery = surgeryRepository.save(SurgeryConverter.toSurgery(dto));
        userRepository.save(UserConverter.toUser(userInfo, userId.toString(), surgery, dto));

        return UserConverter.toUserResponseDTO(userId.toString(), accessToken, refreshToken);
    }

    public UserResponseDTO husbandJoin(String token, UserHusbandJoinRequestDTO dto) {
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(token);
        Long userId = userInfo.getId();

        Optional<User> foundUser = userRepository.findByKeyCode(userId.toString());
        if (foundUser.isPresent()) {
            throw new UserHandler(ALREADY_JOIN);
        }

        // 배우자 코드가 존재하지 않거나, 해당 배우자 코드를 가지고 있는 유저가 이미 남편을 등록해놓은 경우 예외처리
        User wife = userRepository.findBySpouseCode(dto.getSpouseCode()).orElseThrow(() -> new UserHandler(INVALID_SPOUSE_CODE));
        if (wife.getSpouseId() != null) {
            throw new UserHandler(INVALID_SPOUSE_CODE);
        }

        String accessToken = jwtTokenProvider.createToken(userId.toString());
        String refreshToken = jwtTokenProvider.createRefresh(userId.toString());

        User husband = userRepository.save(UserConverter.toHusband(userInfo, userId.toString(), wife, dto));
        wife.setSpouseId(husband.getId());

        try {
            fcmService.sendMessageTo(wife.getFcmToken(), "남편 회원가입 완료 푸시알림", String.format("%s님이 나의 배우자 코드로 가입을 완료했어요. 함께 관리를 시작해볼까요?", husband.getNickname()), NavigationType.daily_hugg_graph.toString(), null, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return UserConverter.toUserResponseDTO(userId.toString(), accessToken, refreshToken);
    }

    public UserResponseDTO renewalAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveToken2(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        String tokenOwnerKeycode = authentication.getName();
        Optional<User> foundUser = userRepository.findByKeyCode(tokenOwnerKeycode);
        foundUser.get().setLastConnect(LocalDateTime.now());
        String redisToken = redisService.getData(tokenOwnerKeycode);

        if (refreshToken.equals(redisToken)) {
            String newAccessToken = jwtTokenProvider.createToken(tokenOwnerKeycode);
            String newRefreshToken = jwtTokenProvider.createRefresh(tokenOwnerKeycode);

            return UserResponseDTO.builder()
                    .keyCode(tokenOwnerKeycode)
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }
        else{
            throw new UserHandler(JWT_WRONG_REFRESHTOKEN);
        }
    }

    public LogoutWithdrawalResponseDTO logout(HttpServletRequest request) {

        String jwt = jwtTokenProvider.resolveToken2(request);

        // 엑세스 토큰이 key로 존재한다면
        if (redisService.isExists(jwt)) {
            throw new UserHandler(LOGOUT_USER);
        }// 존재하지 않는다면

        redisService.deleteData(SecurityUtil.getCurrentUser());
        Long expiration = jwtTokenProvider.getExpiration(jwt);
        redisService.setBlackList(jwt, "accessToken", expiration);
        return LogoutWithdrawalResponseDTO.builder().content("로그아웃 처리 되었습니다").build();

    }

    public LogoutWithdrawalResponseDTO withdrawal(HttpServletRequest request) {
        String jwt = jwtTokenProvider.resolveToken2(request);

        // 아내 계정이면 남편 계정까지 삭제
        if (!SecurityUtil.ifHusband()) {
            User husband = userQueryService.returnSpouse();
            if (husband != null) {
                userRepository.delete(husband);
            }
        }else{
            User wife = userQueryService.returnSpouse();
            wife.setSpouseId(null);
        }

        User user = userQueryService.getUser();
        userRepository.delete(user);

        redisService.deleteData(SecurityUtil.getCurrentUser());
        Long expiration = jwtTokenProvider.getExpiration(jwt);
        redisService.setBlackList(jwt, "withdrawn", expiration);
        return LogoutWithdrawalResponseDTO.builder().content("정상적으로 탈퇴 되었습니다").build();
    }

    public void renewalFcm(FcmRenewalRequest dto) {
        User user = userQueryService.getUser();
        user.setFcmToken(dto.getFcm());
    }

}
