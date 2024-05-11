package foregg.foreggserver.dto.challengeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeMyResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String image;
    private int participants;
    private List<String> successDays;
    private String weekOfMonth;

}
