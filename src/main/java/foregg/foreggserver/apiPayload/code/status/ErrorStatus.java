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

    //유저 관련 에러
    USER_NOT_FOUND(BAD_REQUEST, "USER4001", "존재하지 않거나 승인되지 않은 사용자입니다."),
    USER_NEED_JOIN(BAD_REQUEST, "USER4002", "회원가입이 필요한 사용자입니다."),
    USER_PROFILE_ERROR(BAD_REQUEST, "USER4003", "프로필사진 업로드에 실패했습니다."),
    INVALID_SPOUSE_CODE(BAD_REQUEST, "USER4004", "유효하지 않은 배우자코드입니다"),
    SPOUSE_NOT_FOUND(BAD_REQUEST, "USER4005", "배우자가 존재하지 않습니다"),
    LOGOUT_USER(FORBIDDEN, "USER4006", "로그아웃 된 사용자입니다"),
    ALREADY_JOIN(BAD_REQUEST,"USER4007", "이미 회원가입 된 아이디입니다"),

    //jwt
    JWT_FORBIDDEN(FORBIDDEN, "JWT4001", "권한이 존재하지 않습니다."),
    JWT_UNAUTHORIZED(UNAUTHORIZED, "JWT4002", "자격증명이 유효하지 않습니다."),
    JWT_EXPIRATION(UNAUTHORIZED, "JWT4003", "만료된 jwt 토큰입니다"),
    JWT_WRONG_SIGNATURE(UNAUTHORIZED, "JWT4004", "잘못된 jwt 서명입니다"),
    JWT_WRONG_REFRESHTOKEN(UNAUTHORIZED, "JWT4005", "잘못된 refresh 토큰입니다."),
    JWT_NOT_SUPPORTED(UNAUTHORIZED,"JWT4006", "지원되지 않는 jwt 토큰입니다"),

    //일정 관련 에러
    RECORD_NOT_FOUND(BAD_REQUEST, "RECORD4001", "존재하지 않는 일정입니다"),
    NOT_HOSPITAL_RECORD(BAD_REQUEST, "RECORD4002", "병원 일정이 아닙니다"),
    NOT_REPEAT_TIME(BAD_REQUEST, "RECORD4003", "반복 시간이 존재하지 않습니다"),
    NOT_FOUND_MY_RECORD(BAD_REQUEST, "RECORD4004", "나의 기록이 존재하지 않습니다"),
    NOT_RESERVED_HOSPITAL_RECORD(BAD_REQUEST, "RECORD4005", "예약된 병원 기록이 존재하지 않습니다"),
    NOT_FOUND_REPEATTIME(BAD_REQUEST, "RECORD4006", "해당 시간에 기록이 없습니다"),
    MISMATCH_RECORD_AND_TYPE(BAD_REQUEST, "RECORD4007", "일정의 타입이 일치하지 않습니다"),
    NOT_FOUND_LATEST_MEDICAL_RECORD(BAD_REQUEST, "RECORD4008", "최근의 진료 기록이 없습니다"),
    NOT_FOUND_MY_INJECTION_RECORD(BAD_REQUEST, "RECORD4009","나의 주사 일정을 찾을 수 없습니다"),
    MEN_ONLY_ETC(BAD_REQUEST, "RECORD4010","남편은 기타 일정 외에는 추가할 수 없습니다"),
    INVALID_RECORD_DATE(BAD_REQUEST, "RECORD4011", "해당 날짜에는 일정이 존재하지 않습니다"),


    //챌린지 관련 에러
    CHALLENGE_NOT_FOUND(BAD_REQUEST, "CHALLENGE4002", "존재하지 않는 챌린지입니다"),
    NOT_FOUND_MY_CHALLENGE(BAD_REQUEST, "CHALLENGE4002", "나의 챌린지가 존재하지 않습니다"),
    NO_PARTICIPATING_CHALLENGE(BAD_REQUEST, "CHALLENGE4003", "참여하고 있는 챌린지가 아닙니다"),
    ALREADY_PARTICIPATING(BAD_REQUEST, "CHALLENGE4004", "이미 참여하고 있는 챌린지입니다"),
    DUPLICATED_SUCCESS_DATE(BAD_REQUEST,"CHALLENGE4005", "이미 성공한 날짜입니다"),
    NO_SUCCESS_DAY(BAD_REQUEST, "CHALLENGE4006", "성공한 날짜가 없습니다"),
    NICKNAME_EXIST(BAD_REQUEST, "CHALLENGE4007", "챌린지 닉네임이 이미 존재합니다"),
    NICKNAME_DUPLICATE(BAD_REQUEST, "CHALLENGE4008", "챌린지 닉네임이 중복됩니다"),
    CHALLENGE_NOT_OPEN(BAD_REQUEST, "CHALLENGE4009", "오픈되지 않은 챌린지입니다"),
    ALREADY_OPEN(BAD_REQUEST, "CHALLENGE4010", "이미 오픈된 챌린지입니다"),
    MAKE_NICKNAME_FIRST(BAD_REQUEST, "CHALLENGE4011", "챌린지 닉네임을 먼저 만들어주세요"),
    OUT_OF_VALIDATE_DAYS(BAD_REQUEST, "CHALLENGE4012", "오늘, 어제 날짜 이외에는 챌린지 성공 할 수 없습니다"),
    NO_MORE_THAN_THIRD_TIME(BAD_REQUEST, "CHALLENGE4013", "응원과 박수는 각각 하루에 세 번까지 가능합니다"),
    ALREADY_SEND_CHEER(BAD_REQUEST, "CHALLENGE4014", "이미 오늘 찌르기를 한 유저입니다"),
    UNABLE_TO_SEND_CLAP(BAD_REQUEST, "CHALLENGE4015", "성공하지 않은 유저에게는 박수를 보낼 수 없습니다"),
    UNABLE_TO_SEND_SUPPORT(BAD_REQUEST, "CHALLENGE4016", "성공한 유저에게는 응원을 보낼 수 없습니다"),


    //가계부 관련 에러
    LEDGER_NOT_FOUND(BAD_REQUEST, "LEDGER4001", "존재하지 않는 가계부입니다"),
    NOT_FOUND_MY_LEDGER(BAD_REQUEST, "LEDGER4002", "나의 가계부가 존재하지 않습니다"),
    NOT_MY_LEDGER(BAD_REQUEST, "LEDGER4003", "나의 가계부가 아닙니다"),

    //시술 관련 에러
    NOT_FOUND_MY_SURGERY(BAD_REQUEST, "SURGERY4001", "나의 시술이 존재하지 않습니다"),

    //마이페이지 관련 에러
    NO_BOARD_FOUND(BAD_REQUEST, "MYPAGE4001", "공지가 존재하지 않습니다"),
    NO_FAQ_FOUND(BAD_REQUEST, "MYPAGE4002", "FAQ가 존재하지 않습니다"),

    //하루기록 관련 에러
    ALREADY_WRITTEN(BAD_REQUEST, "DAILY4001", "오늘의 하루기록이 이미 존재합니다"),
    NOT_FOUND_DAILY(BAD_REQUEST, "DAILY4002","하루 기록이 존재하지 않습니다"),
    NOT_FRIDAY(BAD_REQUEST, "DAILY4003","금요일이 아닙니다"),
    ALREADY_REPLY(BAD_REQUEST, "DAILY4004","이미 답장을 했습니다"),
    ONLY_INJECTION_MEDICINE(BAD_REQUEST, "DAILY_4005", "주사와 약 정보만 제공됩니다"),

    //부작용 관련 에러
    NOT_FOUND_SIDEEFFECT(BAD_REQUEST, "SIDEEFFECT4001", "부작용이 존재하지 않습니다"),

    //주사 관련 에러
    NO_SUCH_INJECTION(BAD_REQUEST, "INJECTION4001", "해당 이름의 주사가 존재하지 않습니다"),

    ALREADY_ISSUED(BAD_REQUEST, "SPOUSECODE4001", "배우자 코드를 이미 발급받으셨습니다"),

    //지원금 관련 에러
    NOT_FOUND_MY_SUBSIDY(BAD_REQUEST, "SUBSIDY4001", "나의 지원금이 존재하지 않습니다"),
    BUDGET_OVER(BAD_REQUEST, "SUBSIDY4002", "지원금의 한도가 초과되었습니다"),
    SUBSIDY_ALREADY_EXIST(BAD_REQUEST, "SUBSIDY4003", "해당 회차의 같은 이름의 지원금이 존재합니다"),

    //지출 관련 에러
    NOT_FOUND_EXPENDITURE(BAD_REQUEST, "EXPENDITURE4001", "해당 지출이 존재하지 않습니다"),

    //포인트 관련 에러
    OVER_BUDGET(BAD_REQUEST, "POINT4001", "포인트가 부족합니다"),

    //페이징 관련 에러
    PAGE_OUT_OF_RANGE(BAD_REQUEST, "PAGE4001", "페이지를 벗어났습니다"),

    //날짜 관련 에러
    INVALID_DATE_RANGE(BAD_REQUEST, "DATE4001", "올바르지 않은 날짜 범위입니다");


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