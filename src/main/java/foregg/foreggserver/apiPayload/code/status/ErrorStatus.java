package foregg.foreggserver.apiPayload.code.status;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관려 에러
    MEMBER_NOT_FOUND(BAD_REQUEST, "MEMBER4001", "존재하지 않거나 승인되지 않은 사용자입니다."),
    MEMBER_PROFILE_ERROR(BAD_REQUEST, "MEMBER4005", "프로필사진 업로드에 실패했습니다."),

    //jwt
    JWT_FORBIDDEN(FORBIDDEN, "JWT4001", "권한이 존재하지 않습니다."),
    JWT_UNAUTHORIZED(UNAUTHORIZED, "JWT4002", "자격증명이 유효하지 않습니다."),
    JWT_EXPIRATION(UNAUTHORIZED, "JWT4003", "만료된 jwt 토큰입니다"),
    JWT_WRONG_SIGNATURE(UNAUTHORIZED, "JWT4004", "잘못된 jwt 서명입니다"),
    JWT_WRONG_REFRESHTOKEN(UNAUTHORIZED, "JWT4005", "잘못된 refresh 토큰입니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}