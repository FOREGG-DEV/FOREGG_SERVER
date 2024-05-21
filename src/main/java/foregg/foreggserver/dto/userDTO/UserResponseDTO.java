package foregg.foreggserver.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserResponseDTO {

    private String keyCode;
    private String accessToken;
    private String refreshToken;

}
