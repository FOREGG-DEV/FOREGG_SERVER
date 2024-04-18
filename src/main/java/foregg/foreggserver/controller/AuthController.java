package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.apiPayload.code.status.SuccessStatus;
import foregg.foreggserver.dto.UserJoinRequestDTO;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.jwt.JwtTokenProvider;
import foregg.foreggserver.service.KakaoRequestService;
import foregg.foreggserver.service.UserQueryService;
import foregg.foreggserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "로그인/회원가입 API")
public class AuthController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final KakaoRequestService kakaoRequestService;
    private final JwtTokenProvider jwtTokenProvider;


    // 카카오 로그인을 위해 회원가입 여부 확인, 이미 회원이면 Jwt 토큰 발급
    @Operation(summary = "카카오 로그인 API")
    @Parameters({
            @Parameter(name = "accessToken", description = "헤더의 엑세스 토큰입니다.")
    })
    @PostMapping("/login")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4002", description = "존재하지 않는 사용자입니다."),
    })
    public ApiResponse<String> login(@RequestHeader(name = "accessToken") String accessToken) {
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(accessToken);
        String jwt = userQueryService.isExist(userInfo.getId().toString());
        return ApiResponse.onSuccess(jwt);
    }


    //회원가입이 완료되었을때 JWT를 반환하게끔
    @Operation(summary = "회원가입 API")
    @Parameters({
            @Parameter(name = "accessToken", description = "헤더의 엑세스 토큰입니다."),
    })
    @PostMapping("/join")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> join(@RequestHeader(name = "accessToken") String accessToken, @RequestBody UserJoinRequestDTO request) {

        String idAndToken = userService.join(accessToken, request);
        return ApiResponse.onSuccess(idAndToken);
    }
}