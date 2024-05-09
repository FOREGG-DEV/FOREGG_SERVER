package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;

import java.util.List;

public class ChallengeConverter {

    public static ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge, int participants) {
        return ChallengeResponseDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .participants(participants)
                .build();
    }

    public static ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge, int participants, List<String> successDays) {
        return ChallengeResponseDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .participants(participants)
                .successDays(successDays)
                .build();
    }

    public static ChallengeParticipation toChallengeParticipation(User user, Challenge challenge) {
        return ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .build();
    }
}
