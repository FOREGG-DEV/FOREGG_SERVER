package foregg.foreggserver.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private String keycode;
    private String accessToken;

}
