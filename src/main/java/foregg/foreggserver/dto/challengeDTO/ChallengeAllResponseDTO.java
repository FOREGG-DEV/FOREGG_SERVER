package foregg.foreggserver.dto.challengeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeAllResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private int participants;
    private boolean ifMine;
}
