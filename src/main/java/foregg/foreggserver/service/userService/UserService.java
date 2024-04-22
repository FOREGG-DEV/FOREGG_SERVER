package foregg.foreggserver.service.userService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.SurgeryConverter;
import foregg.foreggserver.converter.UserConverter;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.jwt.JwtTokenProvider;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.SurgeryRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.userService.KakaoRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.USER_NEED_JOIN;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SurgeryRepository surgeryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserQueryService userQueryService;
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
        userRepository.save(UserConverter.toUser(userInfo, keyCode, surgery));

        return UserConverter.toUserResponseDTO(keyCode, jwt);
    }

}
