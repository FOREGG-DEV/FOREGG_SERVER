package foregg.foreggserver.dto.challengeDTO;

import foregg.foreggserver.domain.enums.ChallengeEmojiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class ChallengeRequestDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeNameRequestDTO{
        private String challengeNickname;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeCreateRequestDTO{
        private ChallengeEmojiType challengeEmojiType;
        private String name;
        private String description;
    }

}
