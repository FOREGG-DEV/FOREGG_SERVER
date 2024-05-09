package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.userDTO.UserHusbandJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.service.userService.KakaoRequestService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.service.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "로그인/회원가입 API")
public class AuthController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final KakaoRequestService kakaoRequestService;


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
    public ApiResponse<Object> login(@RequestHeader(name = "accessToken") String accessToken) {
        KakaoUserInfoResponse userInfo = kakaoRequestService.getUserInfo(accessToken);
        UserResponseDTO responseDTO = userQueryService.isExist(userInfo.getId().toString());
        if (responseDTO.getAccessToken() == null) {
            return ApiResponse.onFailureOnLogin(responseDTO);
        }
        return ApiResponse.onSuccess(responseDTO);
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
    public ApiResponse<UserResponseDTO> join(@RequestHeader(name = "accessToken") String accessToken,
                                             @RequestBody UserJoinRequestDTO request) {

        UserResponseDTO responseDTO = userService.join(accessToken, request);
        return ApiResponse.onSuccess(responseDTO);
    }

    @Operation(summary = "남편 회원가입 API")
    @Parameters({
            @Parameter(name = "accessToken", description = "헤더의 엑세스 토큰입니다."),
    })
    @PostMapping("/husbandJoin")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4004", description = "유효하지 않은 배우자코드입니다"),
    })
    public ApiResponse<UserResponseDTO> husbandJoin(@RequestHeader(name = "accessToken") String accessToken,
                                           @RequestBody UserHusbandJoinRequestDTO dto) {
        UserResponseDTO responseDTO = userService.husbandJoin(accessToken, dto);
        return ApiResponse.onSuccess(responseDTO);
    }
}