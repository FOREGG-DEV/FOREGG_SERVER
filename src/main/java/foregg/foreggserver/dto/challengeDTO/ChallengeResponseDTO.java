package foregg.foreggserver.dto.challengeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ChallengeResponseDTO {

    private List<ChallengeDTO> dtos;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeDTO{
        private Long id;
        private int point;
        private Object image;
        private String name;
        private String description;
        private int participants;
        private boolean isOpen;
        private boolean isParticipating;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyChallengeDTO {
        private Long id;
        private String name;
        private int participants;
        private List<String> successDays;
    }

}
