package foregg.foreggserver.service.userService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.SurgeryConverter;
import foregg.foreggserver.converter.UserConverter;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.userDTO.UserHusbandJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.jwt.JwtTokenProvider;
import foregg.foreggserver.repository.SurgeryRepository;
import foregg.foreggserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.INVALID_SPOUSE_CODE;

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


    public UserResponseDTO login(String userPk) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String jwt = jwtTokenProvider.createToken(userPk);
        return UserResponseDTO.builder()
                .keycode(userPk)
                .accessToken(jwt)
                .build();
    }

    // DB에 유저 정보 저장하고, JWT 토큰 만들어서 보내주기
    public UserResponseDTO join(String token, UserJoinRequestDTO dto) {
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(token);
        Long userId = userInfo.getId();

        String jwt = jwtTokenProvider.createToken(userId.toString());

        String keyCode = jwtTokenProvider.getUserPk(jwt);

        Surgery surgery = surgeryRepository.save(SurgeryConverter.toSurgery(dto));
        userRepository.save(UserConverter.toUser(userInfo, keyCode, surgery, dto));

        return UserConverter.toUserResponseDTO(keyCode, jwt, null);
    }

    public UserResponseDTO husbandJoin(String token, UserHusbandJoinRequestDTO dto) {
        // 배우자 코드가 존재하지 않거나, 해당 배우자 코드를 가지고 있는 유저가 이미 남편을 등록해놓은 경우 예외처리
        User wife = userRepository.findBySpouseCode(dto.getSpouseCode()).orElseThrow(() -> new UserHandler(INVALID_SPOUSE_CODE));
        if (wife.getSpouse() != null) {
            throw new UserHandler(INVALID_SPOUSE_CODE);
        }
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(token);
        Long userId = userInfo.getId();
        String jwt = jwtTokenProvider.createToken(userId.toString());
        String keyCode = jwtTokenProvider.getUserPk(jwt);
        User husband = UserConverter.toHusband(userInfo, keyCode, wife, dto);

        wife.setSpouse(husband);
        userRepository.save(husband);

        return UserConverter.toUserResponseDTO(keyCode, jwt, null);

    }

}
