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
    public static class MyChallengeTotalDTO {

        List<MyChallengeDTO> dtos;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MyChallengeDTO{
            private Long id;
            private String name;
            private String image;
            private int participants;
            private String startDate;
            private String firstDate;
            private List<String> successDays;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeParticipantsDTO {

        private List<ChallengeParticipantDTO> dto;
        private int currentPage;
        private int totalPage;
        private int totalItems;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeParticipantDTO {
        private Long userId;
        private String nickname;
        private String thoughts;
        private boolean isSupported;
    }

}
