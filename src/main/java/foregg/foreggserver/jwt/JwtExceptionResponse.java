package foregg.foreggserver.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtExceptionResponse {
    private String message;
    private String status;
    private int value;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String convertToJson() {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return "{}"; // 또는 예외가 발생한 경우 빈 JSON 객체 반환
//        }
//    }

    public String convertToJson() {
        return "{\"message\": \"" + message + "\", \"status\": \"" + status + "\", \"code\": " + value + "}";
    }
}

