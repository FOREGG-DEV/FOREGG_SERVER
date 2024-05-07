package foregg.foreggserver.dto.userDTO;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHusbandJoinRequestDTO {
    private String spouseCode;
    private String ssn;
}
