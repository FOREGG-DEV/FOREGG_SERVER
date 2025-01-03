package foregg.foreggserver.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import foregg.foreggserver.apiPayload.code.BaseCode;
import foregg.foreggserver.apiPayload.code.status.ErrorStatus;
import foregg.foreggserver.apiPayload.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T data){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> redirect(T data) {
        return new ApiResponse<>(true, SuccessStatus._MOVED_PERMANENTLY.getCode() , SuccessStatus._MOVED_PERMANENTLY.getMessage(), data);
    }

    public static <T> ApiResponse<T> of(BaseCode code, T data){
        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), data);
    }


    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(false, code, message, data);
    }

    public static <T> ApiResponse<T> onFailureOnLogin(T data){
        return new ApiResponse<>(false, ErrorStatus.USER_NEED_JOIN.getCode(), ErrorStatus.USER_NEED_JOIN.getMessage(), data);
    }

    public static <T> ApiResponse<T> onSuccess(){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(),null);
    }
}
